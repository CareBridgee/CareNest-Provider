package com.carenest.provider.core.network

import android.util.Log
import com.carenest.provider.core.BuildConfig
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.request.takeFrom
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Singleton

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
        authenticationRefreshCoordinator: AuthenticationRefreshCoordinator,
    ): HttpClient = HttpClient(OkHttp) {
        install(WebSockets)

        install(ContentNegotiation) {
            json(json)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val state = kotlinx.coroutines.withTimeoutOrNull(2000) {
                        authenticationSessionStore.state.first { it.credentials?.isComplete == true }
                    } ?: authenticationSessionStore.state.first()
                    val credentials = state.credentials
                    val accessToken = credentials?.accessToken?.takeIf(String::isNotBlank)
                    val refreshToken = credentials?.refreshToken?.takeIf(String::isNotBlank)
                    if (accessToken != null && refreshToken != null) {
                        BearerTokens(accessToken, refreshToken)
                    } else {
                        null
                    }
                }

                refreshTokens {
                    val storedCredentials = authenticationSessionStore.state.first().credentials
                        ?: return@refreshTokens null

                    // Key recovery off the token that actually failed rather than
                    // whatever the store holds now, so a stale 401 can never refresh
                    // (or clear) a session this request never belonged to.
                    val failedCredentials = oldTokens?.accessToken
                        ?.takeIf(String::isNotBlank)
                        ?.let { failedAccessToken ->
                            if (storedCredentials.accessToken == failedAccessToken) {
                                storedCredentials
                            } else {
                                storedCredentials.copy(accessToken = failedAccessToken)
                            }
                        }
                        ?: storedCredentials

                    val recoveryResult = authenticationRefreshCoordinator.recover(failedCredentials) { refreshToken ->
                        client.requestTokenRefresh(refreshToken)
                    }

                    when (recoveryResult) {
                        AuthenticationRecoveryResult.RECOVERED,
                        AuthenticationRecoveryResult.SESSION_CHANGED -> {
                            val latestCredentials = authenticationSessionStore.state.first().credentials
                            val newAccess = latestCredentials?.accessToken?.takeIf(String::isNotBlank)
                            val newRefresh = latestCredentials?.refreshToken?.takeIf(String::isNotBlank)
                            if (newAccess != null && newRefresh != null) {
                                BearerTokens(newAccess, newRefresh)
                            } else {
                                null
                            }
                        }

                        // The coordinator performs the guarded session clear on rejection.
                        AuthenticationRecoveryResult.REJECTED -> null

                        AuthenticationRecoveryResult.TEMPORARILY_UNAVAILABLE -> null
                    }
                }

                sendWithoutRequest { request ->
                    request.isProtectedBackendRequest(BASE_URL)
                }
            }
        }

        install(ForbiddenIdentityPlugin) {
            sessionStore = authenticationSessionStore
            baseUrl = BASE_URL
            refreshCoordinator = authenticationRefreshCoordinator
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

internal class ForbiddenIdentityPluginConfig {
    lateinit var sessionStore: AuthenticationSessionStore
    lateinit var baseUrl: String
    lateinit var refreshCoordinator: AuthenticationRefreshCoordinator
}

private val IdentityRecoveryAttempted = AttributeKey<Unit>("CareNestIdentityRecoveryAttempted")

internal val ForbiddenIdentityPlugin = createClientPlugin(
    name = "ForbiddenIdentityPlugin",
    createConfiguration = ::ForbiddenIdentityPluginConfig,
) {
    val sessionStore = pluginConfig.sessionStore
    val refreshCoordinator = pluginConfig.refreshCoordinator
    val backendHost = Url(pluginConfig.baseUrl).host

    onResponse { response ->
        if (response.status != HttpStatusCode.Forbidden) return@onResponse
        // The plugin scope disallows calling `client` implicitly here.
        val httpClient = response.call.client

        val request = response.call.request
        val nurseId = sessionStore.state.first().session?.nurseId
        if (!request.isCurrentSessionIdentityRequest(backendHost, nurseId)) return@onResponse

        val requestAccessToken = request.headers[HttpHeaders.Authorization].toBearerAccessToken()

        // This 403 came from a request replayed with freshly loaded credentials,
        // so the server is deliberately denying a live session.
        if (request.attributes.contains(IdentityRecoveryAttempted)) {
            val currentCredentials = sessionStore.state.first().credentials ?: return@onResponse
            if (currentCredentials.accessToken == requestAccessToken) {
                sessionStore.clearSessionIfCurrent(currentCredentials)
            }
            return@onResponse
        }

        val currentCredentials = sessionStore.state.first().credentials ?: run {
            sessionStore.clearInvalidSession()
            return@onResponse
        }

        // Some backends answer 403 for expired or denied credentials instead of 401,
        // so give the refresh token one chance before treating this as rejection.
        val recoveryResult =
            if (currentCredentials.accessToken == requestAccessToken || requestAccessToken == null) {
                refreshCoordinator.recover(currentCredentials) { refreshToken ->
                    httpClient.requestTokenRefresh(refreshToken)
                }
            } else {
                // The store rotated past the token this request carried; another
                // caller owns recovery - replay with the latest credentials only.
                AuthenticationRecoveryResult.RECOVERED
            }

        when (recoveryResult) {
            AuthenticationRecoveryResult.REJECTED -> Unit // coordinator cleared dead tokens
            AuthenticationRecoveryResult.TEMPORARILY_UNAVAILABLE -> Unit // keep session, surface the 403
            AuthenticationRecoveryResult.RECOVERED,
            AuthenticationRecoveryResult.SESSION_CHANGED -> {
                // The bearer provider caches its loaded token, so drop the cache and
                // let it re-load from the store when it attaches headers on the replay.
                httpClient.authProvider<BearerAuthProvider>()?.clearToken()
                httpClient.request {
                    takeFrom(request)
                    attributes.put(IdentityRecoveryAttempted, Unit)
                }
            }
        }
    }
}

private fun String?.toBearerAccessToken(): String? =
    this?.split(' ', limit = 2)?.getOrNull(1)?.trim()?.takeIf(String::isNotBlank)

internal fun isProtectedBackendUrl(url: Url, method: HttpMethod, backendHost: String): Boolean {
    val isBackendHost = url.host.isBlank() || url.host.equals(backendHost, ignoreCase = true)
    val path = url.encodedPath.normalizedPath()
    val isPublicRequest = path in PUBLIC_AUTH_PATHS ||
        (method == HttpMethod.Get && path in PUBLIC_GET_PATHS)
    return isBackendHost && path.startsWith("/api/v1/") && !isPublicRequest
}

internal fun HttpRequestBuilder.isProtectedBackendRequest(baseUrl: String): Boolean {
    val backendHost = Url(baseUrl).host
    return isProtectedBackendUrl(url.build(), method, backendHost)
}

internal fun HttpRequestData.isProtectedBackendRequest(baseUrl: String): Boolean {
    val backendHost = Url(baseUrl).host
    return isProtectedBackendUrl(url, method, backendHost)
}

internal fun isCurrentSessionIdentityUrl(
    url: Url,
    method: HttpMethod,
    backendHost: String,
    nurseId: String?,
): Boolean {
    if (!isProtectedBackendUrl(url, method, backendHost)) return false
    val path = url.encodedPath.normalizedPath()
    return path == "/api/v1/users/me" ||
        (!nurseId.isNullOrBlank() && path == "/api/v1/nurses/$nurseId")
}

internal fun io.ktor.client.request.HttpRequest.isCurrentSessionIdentityRequest(
    backendHost: String,
    nurseId: String?,
): Boolean = isCurrentSessionIdentityUrl(url, method, backendHost, nurseId)

internal fun HttpRequestData.isCurrentSessionIdentityRequest(
    backendHost: String,
    nurseId: String?,
): Boolean = isCurrentSessionIdentityUrl(url, method, backendHost, nurseId)

private fun String.normalizedPath(): String {
    val withLeadingSlash = if (startsWith('/')) this else "/$this"
    return if (withLeadingSlash.length > 1) withLeadingSlash.trimEnd('/') else withLeadingSlash
}

private val PUBLIC_AUTH_PATHS = setOf(
    "/api/v1/auth/nurse/login",
    "/api/v1/auth/nurse/verify-otp",
    "/api/v1/auth/dev/request-otp",
    "/api/v1/auth/refresh",
    "/api/v1/auth/nurse/google",
    "/api/v1/auth/verify-otp",
)

private val PUBLIC_GET_PATHS = setOf(
    "/api/v1/service-types",
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

