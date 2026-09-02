package com.carenest.provider.core.network

import com.carenest.provider.core.datastore.AuthenticationCredentials
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.AuthCircuitBreaker
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)

internal sealed interface TokenRefreshAttempt {
    data class Success(val tokens: RefreshTokenResponse) : TokenRefreshAttempt
    data object Rejected : TokenRefreshAttempt
    data object TemporarilyUnavailable : TokenRefreshAttempt
}

internal enum class AuthenticationRecoveryResult {
    RECOVERED,
    SESSION_CHANGED,
    REJECTED,
    TEMPORARILY_UNAVAILABLE,
}

/**
 * Serializes refresh attempts from REST and WebSocket traffic and only mutates the
 * credentials that originally failed. This prevents an old request from replacing
 * or clearing a newer account session.
 */
@Singleton
class AuthenticationRefreshCoordinator @Inject constructor(
    private val sessionStore: AuthenticationSessionStore,
) {
    private val refreshMutex = Mutex()

    internal suspend fun recover(
        failedCredentials: AuthenticationCredentials,
        requestRefresh: suspend (String) -> TokenRefreshAttempt,
    ): AuthenticationRecoveryResult = refreshMutex.withLock {
        val currentCredentials = sessionStore.state.first().credentials
            ?: return@withLock AuthenticationRecoveryResult.SESSION_CHANGED

        if (currentCredentials.sessionId != failedCredentials.sessionId) {
            return@withLock AuthenticationRecoveryResult.SESSION_CHANGED
        }

        if (currentCredentials.accessToken != failedCredentials.accessToken) {
            return@withLock if (currentCredentials.isComplete) {
                AuthenticationRecoveryResult.RECOVERED
            } else {
                AuthenticationRecoveryResult.SESSION_CHANGED
            }
        }

        val refreshToken = currentCredentials.refreshToken?.takeIf(String::isNotBlank)
        if (refreshToken == null) {
            sessionStore.clearSessionIfCurrent(currentCredentials)
            return@withLock AuthenticationRecoveryResult.REJECTED
        }

        when (val attempt = requestRefresh(refreshToken)) {
            TokenRefreshAttempt.Rejected ->
                confirmRejection(currentCredentials, refreshToken, requestRefresh)

            TokenRefreshAttempt.TemporarilyUnavailable ->
                AuthenticationRecoveryResult.TEMPORARILY_UNAVAILABLE

            is TokenRefreshAttempt.Success ->
                storeRefreshedTokens(currentCredentials, attempt.tokens)
        }
    }

    /**
     * A single 400/401/403 from the refresh endpoint can come from infrastructure
     * between the app and the auth service (WAF rules, rate limiting, warm-up
     * failures). Confirm once before destroying the session: a genuinely dead
     * refresh token gets rejected again, while an infra blip usually clears.
     */
    private suspend fun confirmRejection(
        currentCredentials: AuthenticationCredentials,
        refreshToken: String,
        requestRefresh: suspend (String) -> TokenRefreshAttempt,
    ): AuthenticationRecoveryResult =
        when (val confirmation = requestRefresh(refreshToken)) {
            TokenRefreshAttempt.Rejected -> {
                sessionStore.clearSessionIfCurrent(currentCredentials)
                AuthenticationRecoveryResult.REJECTED
            }

            TokenRefreshAttempt.TemporarilyUnavailable ->
                AuthenticationRecoveryResult.TEMPORARILY_UNAVAILABLE

            is TokenRefreshAttempt.Success ->
                storeRefreshedTokens(currentCredentials, confirmation.tokens)
        }

    private suspend fun storeRefreshedTokens(
        currentCredentials: AuthenticationCredentials,
        tokens: RefreshTokenResponse,
    ): AuthenticationRecoveryResult =
        when {
            tokens.accessToken.isBlank() || tokens.refreshToken.isBlank() ->
                AuthenticationRecoveryResult.TEMPORARILY_UNAVAILABLE

            sessionStore.replaceCredentials(
                expectedCredentials = currentCredentials,
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken,
            ) -> AuthenticationRecoveryResult.RECOVERED

            else -> AuthenticationRecoveryResult.SESSION_CHANGED
        }
}

internal suspend fun HttpClient.requestTokenRefresh(refreshToken: String): TokenRefreshAttempt =
    runCatching {
        val response = post("/api/v1/auth/refresh") {
            // Without this marker the Auth plugin re-sends the refresh request once
            // when the endpoint itself answers 401, doubling server round-trips.
            attributes.put(AuthCircuitBreaker, Unit)
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken))
        }

        when {
            response.status.isSuccess() -> TokenRefreshAttempt.Success(response.body())
            CredentialRejection.isCandidateStatus(response.status) -> TokenRefreshAttempt.Rejected
            else -> TokenRefreshAttempt.TemporarilyUnavailable
        }
    }.getOrElse {
        TokenRefreshAttempt.TemporarilyUnavailable
    }
