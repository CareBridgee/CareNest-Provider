package com.carenest.provider.core.network

import com.carenest.provider.core.datastore.AuthenticationCredentials
import com.carenest.provider.core.datastore.AuthenticationSession
import com.carenest.provider.core.datastore.AuthenticationSessionDestination
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.datastore.AuthenticationState
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.HttpRequestData
import kotlinx.coroutines.flow.first
import io.ktor.client.request.post
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DynamicAuthenticationPluginTest {

    @Test
    fun protectedRequestRefreshesOnceAndRetriesWithRotatedTokenPair() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("old-access", "old-refresh", "nurse-a")
        }
        val refreshCalls = AtomicInteger(0)
        val protectedAuthorizationHeaders = mutableListOf<String?>()
        val client = testClient(store) { request ->
            when (request.url.encodedPath) {
                "/api/v1/auth/refresh" -> {
                    refreshCalls.incrementAndGet()
                    respondJson(
                        """{"accessToken":"new-access","refreshToken":"new-refresh"}""",
                    )
                }
                "/api/v1/users/me" -> {
                    val authorization = request.headers[HttpHeaders.Authorization]
                    protectedAuthorizationHeaders += authorization
                    if (authorization == "Bearer new-access") {
                        respondJson("{}")
                    } else {
                        respond("", HttpStatusCode.Unauthorized)
                    }
                }
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }

        val response = client.get("/api/v1/users/me")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(1, refreshCalls.get())
        assertEquals(listOf("Bearer old-access", "Bearer new-access"), protectedAuthorizationHeaders)
        val credentials = store.state.value.credentials
        assertEquals("new-access", credentials?.accessToken)
        assertEquals("new-refresh", credentials?.refreshToken)
        assertTrue(store.state.value.isAuthenticated)
        client.close()
    }

    @Test
    fun identityForbiddenStillForbiddenAfterRefreshClearsFreshSession() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("expired-access", "valid-refresh", "nurse-a")
        }
        val refreshCalls = AtomicInteger(0)
        val identityAuthorizationHeaders = mutableListOf<String?>()
        val client = testClient(store) { request ->
            when (request.url.encodedPath) {
                "/api/v1/auth/refresh" -> {
                    refreshCalls.incrementAndGet()
                    respondJson(
                        """{"accessToken":"new-access","refreshToken":"new-refresh"}""",
                    )
                }
                "/api/v1/nurses/nurse-a" -> {
                    identityAuthorizationHeaders += request.headers[HttpHeaders.Authorization]
                    respond("", HttpStatusCode.Forbidden)
                }
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }

        val response = client.get("/api/v1/nurses/nurse-a")

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals(1, refreshCalls.get())
        assertEquals(listOf("Bearer expired-access", "Bearer new-access"), identityAuthorizationHeaders)
        assertFalse(store.state.value.isAuthenticated)
        assertNull(store.state.value.credentials)
        assertNull(store.state.value.session)
        client.close()
    }

    @Test
    fun identityForbiddenRecoversWhenReplaySucceeds() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("expired-access", "valid-refresh", "nurse-a")
        }
        val refreshCalls = AtomicInteger(0)
        val identityAuthorizationHeaders = mutableListOf<String?>()
        val client = testClient(store) { request ->
            when (request.url.encodedPath) {
                "/api/v1/auth/refresh" -> {
                    refreshCalls.incrementAndGet()
                    respondJson(
                        """{"accessToken":"new-access","refreshToken":"new-refresh"}""",
                    )
                }
                "/api/v1/users/me" -> {
                    val authorization = request.headers[HttpHeaders.Authorization]
                    identityAuthorizationHeaders += authorization
                    if (authorization == "Bearer new-access") {
                        respondJson("{}")
                    } else {
                        respond("", HttpStatusCode.Forbidden)
                    }
                }
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }

        val response = client.get("/api/v1/users/me")

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals(1, refreshCalls.get())
        assertEquals(listOf("Bearer expired-access", "Bearer new-access"), identityAuthorizationHeaders)
        assertTrue(store.state.value.isAuthenticated)
        assertEquals("new-access", store.state.value.credentials?.accessToken)
        assertEquals("new-refresh", store.state.value.credentials?.refreshToken)
        client.close()
    }

    @Test
    fun identityForbiddenWithRejectedRefreshClearsSession() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("expired-access", "dead-refresh", "nurse-a")
        }
        val refreshCalls = AtomicInteger(0)
        val identityAuthorizationHeaders = mutableListOf<String?>()
        val client = testClient(store) { request ->
            when (request.url.encodedPath) {
                "/api/v1/auth/refresh" -> {
                    refreshCalls.incrementAndGet()
                    respond("", HttpStatusCode.Unauthorized)
                }
                "/api/v1/users/me" -> {
                    identityAuthorizationHeaders += request.headers[HttpHeaders.Authorization]
                    respond("", HttpStatusCode.Forbidden)
                }
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }

        val response = client.get("/api/v1/users/me")

        assertEquals(HttpStatusCode.Forbidden, response.status)
        // Rejection is confirmed with a second refresh attempt before the session dies.
        assertEquals(2, refreshCalls.get())
        assertEquals(listOf("Bearer expired-access"), identityAuthorizationHeaders)
        assertFalse(store.state.value.isAuthenticated)
        assertNull(store.state.value.credentials)
        client.close()
    }

    @Test
    fun identityForbiddenWithUnavailableRefreshKeepsSession() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("expired-access", "valid-refresh", "nurse-a")
        }
        val refreshCalls = AtomicInteger(0)
        val identityAuthorizationHeaders = mutableListOf<String?>()
        val client = testClient(store) { request ->
            when (request.url.encodedPath) {
                "/api/v1/auth/refresh" -> {
                    refreshCalls.incrementAndGet()
                    respond("", HttpStatusCode.InternalServerError)
                }
                "/api/v1/users/me" -> {
                    identityAuthorizationHeaders += request.headers[HttpHeaders.Authorization]
                    respond("", HttpStatusCode.Forbidden)
                }
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }

        val response = client.get("/api/v1/users/me")

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals(1, refreshCalls.get())
        assertEquals(listOf("Bearer expired-access"), identityAuthorizationHeaders)
        assertTrue(store.state.value.isAuthenticated)
        assertEquals("expired-access", store.state.value.credentials?.accessToken)
        assertEquals("valid-refresh", store.state.value.credentials?.refreshToken)
        client.close()
    }

    @Test
    fun identityForbiddenOnStaleTokenReplaysWithoutExtraRefresh() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("stale-access", "stale-refresh", "nurse-a")
        }
        val refreshCalls = AtomicInteger(0)
        val identityAuthorizationHeaders = mutableListOf<String?>()
        val client = testClient(store) { request ->
            when (request.url.encodedPath) {
                "/api/v1/auth/refresh" -> {
                    refreshCalls.incrementAndGet()
                    respondJson(
                        """{"accessToken":"rotated-access","refreshToken":"rotated-refresh"}""",
                    )
                }
                "/api/v1/users/me" -> {
                    val authorization = request.headers[HttpHeaders.Authorization]
                    identityAuthorizationHeaders += authorization
                    if (authorization == "Bearer rotated-access") {
                        respondJson("{}")
                    } else {
                        // Simulate a concurrent rotation completing while this request is in flight.
                        val current = requireNotNull(store.state.first().credentials)
                        store.replaceCredentials(current, "rotated-access", "rotated-refresh")
                        respond("", HttpStatusCode.Forbidden)
                    }
                }
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }

        val response = client.get("/api/v1/users/me")

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals(0, refreshCalls.get())
        assertEquals(listOf("Bearer stale-access", "Bearer rotated-access"), identityAuthorizationHeaders)
        assertTrue(store.state.value.isAuthenticated)
        client.close()
    }

    @Test
    fun ordinaryForbiddenResponseDoesNotRefreshOrClearValidSession() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("current-access", "current-refresh", "nurse-a")
        }
        val refreshCalls = AtomicInteger(0)
        val client = testClient(store) { request ->
            if (request.url.encodedPath == "/api/v1/auth/refresh") {
                refreshCalls.incrementAndGet()
            }
            respond("", HttpStatusCode.Forbidden)
        }

        val response = client.get("/api/v1/nurses/another-nurse")

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals(0, refreshCalls.get())
        assertTrue(store.state.value.isAuthenticated)
        client.close()
    }

    @Test
    fun failedRefreshClearsEntireSession() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("expired-access", "invalid-refresh", "nurse-a")
        }
        val client = testClient(store) { request ->
            when (request.url.encodedPath) {
                "/api/v1/auth/refresh" -> respond("", HttpStatusCode.Unauthorized)
                "/api/v1/users/me" -> respond("", HttpStatusCode.Unauthorized)
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }

        val response = client.get("/api/v1/users/me")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertFalse(store.state.value.isAuthenticated)
        assertNull(store.state.value.credentials)
        assertNull(store.state.value.session)
        client.close()
    }

    @Test
    fun temporaryRefreshServerFailurePreservesSession() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("expired-access", "valid-refresh", "nurse-a")
        }
        val client = testClient(store) { request ->
            when (request.url.encodedPath) {
                "/api/v1/auth/refresh" -> respond("", HttpStatusCode.InternalServerError)
                "/api/v1/users/me" -> respond("", HttpStatusCode.Unauthorized)
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }

        val response = client.get("/api/v1/users/me")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue(store.state.value.isAuthenticated)
        assertEquals("expired-access", store.state.value.credentials?.accessToken)
        assertEquals("valid-refresh", store.state.value.credentials?.refreshToken)
        client.close()
    }

    @Test
    fun missingRefreshTokenClearsInvalidSessionWithoutRefreshLoop() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            setState(
                AuthenticationState(
                    credentials = AuthenticationCredentials(
                        accessToken = "expired-access",
                        refreshToken = null,
                        sessionId = "session-1",
                    ),
                    session = AuthenticationSession(
                        destination = AuthenticationSessionDestination.APPROVED,
                        nurseId = "nurse-a",
                    ),
                ),
            )
        }
        val refreshCalls = AtomicInteger(0)
        val client = testClient(store) { request ->
            if (request.url.encodedPath == "/api/v1/auth/refresh") {
                refreshCalls.incrementAndGet()
            }
            respond("", HttpStatusCode.Unauthorized)
        }

        client.get("/api/v1/users/me")

        assertEquals(0, refreshCalls.get())
        assertNull(store.state.value.credentials)
        assertNull(store.state.value.session)
        client.close()
    }

    @Test
    fun subsequentRequestsUseRefreshedTokenFromBearerPlugin() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("old-access", "old-refresh", "nurse-a")
        }
        val authorizationHeaders = mutableListOf<String?>()
        val client = testClient(store) { request ->
            when (request.url.encodedPath) {
                "/api/v1/auth/refresh" -> respondJson("""{"accessToken":"new-access","refreshToken":"new-refresh"}""")
                "/api/v1/users/me" -> {
                    val auth = request.headers[HttpHeaders.Authorization]
                    authorizationHeaders += auth
                    if (auth == "Bearer new-access") respondJson("{}") else respond("", HttpStatusCode.Unauthorized)
                }
                else -> error("Unexpected path ${request.url.encodedPath}")
            }
        }

        client.get("/api/v1/users/me")
        client.get("/api/v1/users/me")

        assertEquals(listOf("Bearer old-access", "Bearer new-access", "Bearer new-access"), authorizationHeaders)
        client.close()
    }

    @Test
    fun publicAuthenticationRequestsNeverReceiveBearerHeader() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("current-access", "current-refresh", "nurse-a")
        }
        var authorizationHeader: String? = "not-called"
        val client = testClient(store) { request ->
            authorizationHeader = request.headers[HttpHeaders.Authorization]
            respondJson("{}")
        }

        client.post("/api/v1/auth/nurse/login")

        assertNull(authorizationHeader)
        client.close()
    }

    @Test
    fun publicServiceTypesGetDoesNotReceiveBearerHeader() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("current-access", "current-refresh", "nurse-a")
        }
        var authorizationHeader: String? = "not-called"
        val client = testClient(store) { request ->
            authorizationHeader = request.headers[HttpHeaders.Authorization]
            respondJson("[]")
        }

        client.get("/api/v1/service-types")

        assertNull(authorizationHeader)
        client.close()
    }

    @Test
    fun serviceTypesMutationRemainsAuthenticated() = runBlocking {
        val store = FakeAuthenticationSessionStore().apply {
            authenticate("current-access", "current-refresh", "nurse-a")
        }
        var authorizationHeader: String? = null
        val client = testClient(store) { request ->
            authorizationHeader = request.headers[HttpHeaders.Authorization]
            respondJson("{}")
        }

        client.post("/api/v1/service-types")

        assertEquals("Bearer current-access", authorizationHeader)
        client.close()
    }

    private fun testClient(
        store: AuthenticationSessionStore,
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): HttpClient {
        val refreshCoordinator = AuthenticationRefreshCoordinator(store)
        return HttpClient(MockEngine { request -> handler(request) }) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val credentials = store.state.first().credentials
                    val accessToken = credentials?.accessToken?.takeIf(String::isNotBlank)
                    val refreshToken = credentials?.refreshToken?.takeIf(String::isNotBlank)
                    if (accessToken != null && refreshToken != null) {
                        BearerTokens(accessToken, refreshToken)
                    } else {
                        null
                    }
                }
                refreshTokens {
                    val storedCredentials = store.state.first().credentials
                        ?: return@refreshTokens null
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
                    val recoveryResult = refreshCoordinator.recover(failedCredentials) { refreshToken ->
                        client.requestTokenRefresh(refreshToken)
                    }
                    when (recoveryResult) {
                        AuthenticationRecoveryResult.RECOVERED,
                        AuthenticationRecoveryResult.SESSION_CHANGED -> {
                            val latestCredentials = store.state.first().credentials
                            val newAccess = latestCredentials?.accessToken?.takeIf(String::isNotBlank)
                            val newRefresh = latestCredentials?.refreshToken?.takeIf(String::isNotBlank)
                            if (newAccess != null && newRefresh != null) {
                                BearerTokens(newAccess, newRefresh)
                            } else {
                                null
                            }
                        }
                        AuthenticationRecoveryResult.REJECTED -> {
                            store.clearSessionIfCurrent(failedCredentials)
                            null
                        }
                        AuthenticationRecoveryResult.TEMPORARILY_UNAVAILABLE -> null
                    }
                }
                sendWithoutRequest { request ->
                    request.isProtectedBackendRequest(TEST_BASE_URL)
                }
            }
        }
        install(ForbiddenIdentityPlugin) {
            sessionStore = store
            baseUrl = TEST_BASE_URL
            this.refreshCoordinator = refreshCoordinator
        }
        defaultRequest { url(TEST_BASE_URL) }
        }
    }
    private fun MockRequestHandleScope.respondJson(body: String) = respond(
        content = body,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )

    private class FakeAuthenticationSessionStore : AuthenticationSessionStore {
        private val mutableState = MutableStateFlow(AuthenticationState())
        override val state = mutableState.asStateFlow()
        override val session: Flow<AuthenticationSession?> = MutableStateFlow(null)
        override val currentSession: AuthenticationSession?
            get() = mutableState.value.session
        private var generation = 0

        fun setState(state: AuthenticationState) {
            mutableState.value = state
        }

        suspend fun authenticate(accessToken: String, refreshToken: String, nurseId: String) {
            beginAuthentication(accessToken, refreshToken)
            val credentials = requireNotNull(mutableState.value.credentials)
            completeAuthentication(
                expectedCredentials = credentials,
                session = AuthenticationSession(
                    destination = AuthenticationSessionDestination.APPROVED,
                    nurseId = nurseId,
                ),
            )
        }

        override suspend fun beginAuthentication(accessToken: String, refreshToken: String) {
            generation += 1
            mutableState.value = AuthenticationState(
                credentials = AuthenticationCredentials(accessToken, refreshToken, "session-$generation"),
            )
        }

        override suspend fun completeAuthentication(
            expectedCredentials: AuthenticationCredentials,
            session: AuthenticationSession,
        ): Boolean {
            val current = mutableState.value
            if (current.credentials != expectedCredentials || !expectedCredentials.isComplete) return false
            mutableState.value = current.copy(session = session)
            return true
        }

        override suspend fun replaceCredentials(
            expectedCredentials: AuthenticationCredentials,
            accessToken: String,
            refreshToken: String,
        ): Boolean {
            val current = mutableState.value
            if (current.credentials != expectedCredentials) return false
            mutableState.value = current.copy(
                credentials = expectedCredentials.copy(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                ),
            )
            return true
        }

        override suspend fun updateProfileImageUrl(
            nurseId: String,
            profileImageUrl: String?,
        ): Boolean {
            val current = mutableState.value
            val currentSession = current.session ?: return false
            if (currentSession.nurseId != nurseId) return false
            mutableState.value = current.copy(
                session = currentSession.copy(profileImageUrl = profileImageUrl),
            )
            return true
        }

        override suspend fun clearSession() {
            mutableState.value = AuthenticationState()
        }

        override suspend fun clearInvalidSession(): Boolean {
            val current = mutableState.value
            if ((current.credentials != null || current.session != null) && !current.isAuthenticated) {
                clearSession()
                return true
            }
            return false
        }

        override suspend fun clearSessionIfCurrent(
            expectedCredentials: AuthenticationCredentials,
        ): Boolean {
            if (mutableState.value.credentials != expectedCredentials) return false
            clearSession()
            return true
        }
    }

    private companion object {
        const val TEST_BASE_URL = "https://provider.test/"
    }
}
