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
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
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
                    val failedCredentials = authenticationSessionStore.state.first().credentials
                        ?: return@refreshTokens null

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

                        AuthenticationRecoveryResult.REJECTED -> {
                            authenticationSessionStore.clearSessionIfCurrent(failedCredentials)
                            null
                        }

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
}

internal val ForbiddenIdentityPlugin = createClientPlugin(
    name = "ForbiddenIdentityPlugin",
    createConfiguration = ::ForbiddenIdentityPluginConfig,
) {
    val sessionStore = pluginConfig.sessionStore
    val backendHost = Url(pluginConfig.baseUrl).host

    onResponse { response ->
        if (response.status.value == 403) {
            val request = response.call.request
            if (request.isCurrentSessionIdentityRequest(
                    backendHost = backendHost,
                    nurseId = sessionStore.state.first().session?.nurseId,
                )
            ) {
                val currentCredentials = sessionStore.state.first().credentials
                if (currentCredentials != null) {
                    sessionStore.clearSessionIfCurrent(currentCredentials)
                } else {
                    sessionStore.clearInvalidSession()
                }
            }
        }
    }
}

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

