package com.carenest.provider.core.network

import com.carenest.provider.core.BuildConfig
import com.carenest.provider.core.datastore.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class RefreshTokenResponse(val accessToken: String, val refreshToken: String)

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
        tokenManager: TokenManager
    ): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            level = LogLevel.BODY
        }

        install(Auth) {
            bearer {
                sendWithoutRequest { request ->
                    val path = request.url.encodedPath
                    !path.contains("/auth/")
                }

                loadTokens {
                    val accessToken = tokenManager.accessToken.first()
                    val refreshToken = tokenManager.refreshToken.first()
                    if (accessToken != null && refreshToken != null) {
                        BearerTokens(accessToken, refreshToken)
                    } else null
                }

                refreshTokens {
                    val refreshToken = tokenManager.refreshToken.first() ?: return@refreshTokens null
                    
                    val response = client.post(BASE_URL + "api/v1/auth/refresh") {
                        contentType(ContentType.Application.Json)
                        setBody(RefreshTokenRequest(refreshToken))
                        markAsRefreshTokenRequest()
                    }

                    if (response.status.value == 200 || response.status.value == 201) {
                        val newTokens = response.body<RefreshTokenResponse>()
                        tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                        BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                    } else {
                        tokenManager.clearTokens()
                        null
                    }
                }
            }
        }

        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            headers.append("User-Agent", "CareNestProviderApp/1.0")
        }
    }
}
