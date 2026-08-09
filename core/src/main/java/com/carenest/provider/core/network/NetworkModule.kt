package com.carenest.provider.core.network

import android.util.Log
import com.carenest.provider.core.BuildConfig
import com.carenest.provider.core.datastore.AuthenticationCredentials
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

import io.ktor.client.plugins.websocket.WebSockets

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json,
        authenticationSessionStore: AuthenticationSessionStore,
    ): HttpClient = HttpClient(OkHttp) {
        install(WebSockets)

        install(ContentNegotiation) {
            json(json)
        }

        install(DynamicAuthenticationPlugin) {
            sessionStore = authenticationSessionStore
            baseUrl = BASE_URL
        }

        if (BuildConfig.DEBUG) {
            install(SafeNetworkLogging)
        }

        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            headers.append("User-Agent", "CareNestProviderApp/1.0")
        }
    }
}

internal class DynamicAuthenticationPluginConfig {
    lateinit var sessionStore: AuthenticationSessionStore
    lateinit var baseUrl: String
}

internal val DynamicAuthenticationPlugin = createClientPlugin(
    name = "DynamicAuthenticationPlugin",
    createConfiguration = ::DynamicAuthenticationPluginConfig,
) {
    val sessionStore = pluginConfig.sessionStore
    val backendHost = Url(pluginConfig.baseUrl).host
    val refreshMutex = Mutex()

    suspend fun refreshCurrentCredentials(
        credentials: AuthenticationCredentials,
        refreshToken: String,
    ): Boolean {
        val refreshedTokens = runCatching {
            val response = client.post("/api/v1/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken))
            }
            if (!response.status.isSuccess()) return@runCatching null

            response.body<RefreshTokenResponse>().takeIf {
                it.accessToken.isNotBlank() && it.refreshToken.isNotBlank()
            }
        }.getOrNull()

        if (refreshedTokens == null) {
            sessionStore.clearSessionIfCurrent(credentials)
            return false
        }

        return sessionStore.replaceCredentials(
            expectedCredentials = credentials,
            accessToken = refreshedTokens.accessToken,
            refreshToken = refreshedTokens.refreshToken,
        )
    }

    onRequest { request, _ ->
        if (!request.isProtectedBackendRequest(backendHost)) return@onRequest

        request.headers.remove(HttpHeaders.Authorization)
        val credentials = sessionStore.state.first().credentials
        credentials?.accessToken?.takeIf(String::isNotBlank)?.let { accessToken ->
            request.headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
            request.attributes.put(
                RequestAuthenticationKey,
                RequestAuthentication(
                    accessToken = accessToken,
                    sessionId = credentials.sessionId,
                ),
            )
        }
    }

    on(Send) { request ->
        val originalCall = proceed(request)
        val responseStatus = originalCall.response.status.value
        val requestAuthentication = request.attributes.getOrNull(RequestAuthenticationKey)

        if (responseStatus == 403 && request.isCurrentSessionIdentityRequest(
                backendHost = backendHost,
                nurseId = sessionStore.state.first().session?.nurseId,
            )
        ) {
            val currentCredentials = sessionStore.state.first().credentials
            if (
                currentCredentials != null &&
                currentCredentials.sessionId == requestAuthentication?.sessionId
            ) {
                sessionStore.clearSessionIfCurrent(currentCredentials)
            } else {
                sessionStore.clearInvalidSession()
            }
            return@on originalCall
        }

        val shouldRecover = responseStatus == 401 &&
                request.isProtectedBackendRequest(backendHost) &&
                request.attributes.getOrNull(AuthenticationRetryKey) != true

        if (!shouldRecover) return@on originalCall

        if (requestAuthentication == null) {
            sessionStore.clearInvalidSession()
            return@on originalCall
        }

        val canRetry = refreshMutex.withLock {
            val currentCredentials = sessionStore.state.first().credentials
                ?: return@withLock false

            if (currentCredentials.sessionId != requestAuthentication.sessionId) {
                return@withLock false
            }

            if (currentCredentials.accessToken != requestAuthentication.accessToken) {
                return@withLock currentCredentials.isComplete
            }

            val refreshToken = currentCredentials.refreshToken?.takeIf(String::isNotBlank)
            if (refreshToken == null) {
                sessionStore.clearSessionIfCurrent(currentCredentials)
                return@withLock false
            }

            refreshCurrentCredentials(
                credentials = currentCredentials,
                refreshToken = refreshToken,
            )
        }

        if (!canRetry) return@on originalCall

        val latestCredentials = sessionStore.state.first().credentials
        val latestAccessToken = latestCredentials?.accessToken?.takeIf(String::isNotBlank)
        if (latestCredentials?.sessionId != requestAuthentication.sessionId || latestAccessToken == null) {
            return@on originalCall
        }

        request.attributes.put(AuthenticationRetryKey, true)
        request.headers.remove(HttpHeaders.Authorization)
        request.headers.append(HttpHeaders.Authorization, "Bearer $latestAccessToken")
        proceed(request)
    }

}

private data class RequestAuthentication(
    val accessToken: String,
    val sessionId: String?,
)

private val RequestAuthenticationKey =
    AttributeKey<RequestAuthentication>("CareNestProviderRequestAuthentication")
private val AuthenticationRetryKey =
    AttributeKey<Boolean>("CareNestProviderAuthenticationRetry")

private fun HttpRequestBuilder.isProtectedBackendRequest(backendHost: String): Boolean {
    val requestUrl = url.build()
    val isBackendHost = requestUrl.host.isBlank() || requestUrl.host.equals(backendHost, ignoreCase = true)
    val path = requestUrl.encodedPath.normalizedPath()
    return isBackendHost && path.startsWith("/api/v1/") && path !in PUBLIC_AUTH_PATHS
}

private fun HttpRequestBuilder.isCurrentSessionIdentityRequest(
    backendHost: String,
    nurseId: String?,
): Boolean {
    if (!isProtectedBackendRequest(backendHost)) return false

    val path = url.build().encodedPath.normalizedPath()
    return path == "/api/v1/users/me" ||
            (!nurseId.isNullOrBlank() && path == "/api/v1/nurses/$nurseId")
}

private fun String.normalizedPath(): String {
    val withLeadingSlash = if (startsWith('/')) this else "/$this"
    return if (withLeadingSlash.length > 1) withLeadingSlash.trimEnd('/') else withLeadingSlash
}

private val PUBLIC_AUTH_PATHS = setOf(
    "/api/v1/auth/nurse/login",
    "/api/v1/auth/nurse/verify-otp",
    "/api/v1/auth/dev/request-otp",
    "/api/v1/auth/refresh",
)

private val SafeNetworkLogging = createClientPlugin("SafeNetworkLogging") {
    onRequest { request, _ ->
        val url = request.url.build()
        Log.d(NETWORK_LOG_TAG, "request method=${request.method.value} path=${url.encodedPath}")
    }
    onResponse { response ->
        val request = response.call.request
        Log.d(
            NETWORK_LOG_TAG,
            "response method=${request.method.value} path=${request.url.encodedPath} " +
                    "status=${response.status.value}",
        )
    }
}

private const val NETWORK_LOG_TAG = "CareNestHttp"