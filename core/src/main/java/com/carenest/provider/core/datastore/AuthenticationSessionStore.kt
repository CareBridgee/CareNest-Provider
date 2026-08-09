package com.carenest.provider.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

enum class AuthenticationSessionDestination {
    COMPLETE_PROFILE,
    UNDER_REVIEW,
    REJECTED,
    APPROVED,
}

data class AuthenticationSession(
    val destination: AuthenticationSessionDestination,
    val nurseId: String? = null,
    val phoneNumber: String? = null,
)

data class AuthenticationCredentials(
    val accessToken: String?,
    val refreshToken: String?,
    internal val sessionId: String?,
) {
    val isComplete: Boolean
        get() = !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()
}

data class AuthenticationState(
    val credentials: AuthenticationCredentials? = null,
    val session: AuthenticationSession? = null,
) {
    val isAuthenticated: Boolean
        get() = credentials?.isComplete == true && session != null
}

interface AuthenticationSessionStore {
    val state: Flow<AuthenticationState>
    val session: Flow<AuthenticationSession?>

    suspend fun beginAuthentication(accessToken: String, refreshToken: String)

    suspend fun completeAuthentication(
        expectedCredentials: AuthenticationCredentials,
        session: AuthenticationSession,
    ): Boolean

    suspend fun replaceCredentials(
        expectedCredentials: AuthenticationCredentials,
        accessToken: String,
        refreshToken: String,
    ): Boolean

    suspend fun clearSession()

    suspend fun clearInvalidSession(): Boolean

    suspend fun clearSessionIfCurrent(expectedCredentials: AuthenticationCredentials): Boolean
}

class DataStoreAuthenticationSessionStore @Inject constructor(
    @param:AuthDataStore private val dataStore: DataStore<Preferences>,
) : AuthenticationSessionStore {

    override val state: Flow<AuthenticationState> = dataStore.data.map { preferences ->
        preferences.toAuthenticationState()
    }.distinctUntilChanged()

    override val session: Flow<AuthenticationSession?> = state.map { it.session }
        .distinctUntilChanged()

    override suspend fun beginAuthentication(accessToken: String, refreshToken: String) {
        require(accessToken.isNotBlank()) { "Access token must not be blank" }
        require(refreshToken.isNotBlank()) { "Refresh token must not be blank" }

        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[SESSION_ID_KEY] = UUID.randomUUID().toString()
            preferences.removeSessionMetadata()
        }
    }

    override suspend fun completeAuthentication(
        expectedCredentials: AuthenticationCredentials,
        session: AuthenticationSession,
    ): Boolean {
        var completed = false
        dataStore.edit { preferences ->
            if (expectedCredentials.isComplete && preferences.matches(expectedCredentials)) {
                preferences[DESTINATION_KEY] = session.destination.name
                session.nurseId?.let { preferences[NURSE_ID_KEY] = it }
                    ?: preferences.remove(NURSE_ID_KEY)
                session.phoneNumber?.let { preferences[PHONE_NUMBER_KEY] = it }
                    ?: preferences.remove(PHONE_NUMBER_KEY)
                completed = true
            }
        }
        return completed
    }

    override suspend fun replaceCredentials(
        expectedCredentials: AuthenticationCredentials,
        accessToken: String,
        refreshToken: String,
    ): Boolean {
        if (accessToken.isBlank() || refreshToken.isBlank()) return false

        var replaced = false
        dataStore.edit { preferences ->
            if (preferences.matches(expectedCredentials)) {
                preferences[ACCESS_TOKEN_KEY] = accessToken
                preferences[REFRESH_TOKEN_KEY] = refreshToken
                replaced = true
            }
        }
        return replaced
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences -> preferences.removeAuthenticationState() }
    }

    override suspend fun clearInvalidSession(): Boolean {
        var cleared = false
        dataStore.edit { preferences ->
            val state = preferences.toAuthenticationState()
            val hasPersistedAuthenticationState = state.credentials != null || state.session != null
            if (hasPersistedAuthenticationState && !state.isAuthenticated) {
                preferences.removeAuthenticationState()
                cleared = true
            }
        }
        return cleared
    }

    override suspend fun clearSessionIfCurrent(
        expectedCredentials: AuthenticationCredentials,
    ): Boolean {
        var cleared = false
        dataStore.edit { preferences ->
            if (preferences.matches(expectedCredentials)) {
                preferences.removeAuthenticationState()
                cleared = true
            }
        }
        return cleared
    }

    private fun Preferences.toAuthenticationState(): AuthenticationState {
        val accessToken = this[ACCESS_TOKEN_KEY]
        val refreshToken = this[REFRESH_TOKEN_KEY]
        val sessionId = this[SESSION_ID_KEY]
        val credentials = if (accessToken != null || refreshToken != null || sessionId != null) {
            AuthenticationCredentials(
                accessToken = accessToken,
                refreshToken = refreshToken,
                sessionId = sessionId,
            )
        } else {
            null
        }

        val destination = this[DESTINATION_KEY]
            ?.let { runCatching { AuthenticationSessionDestination.valueOf(it) }.getOrNull() }
        val savedSession = destination?.let {
            AuthenticationSession(
                destination = it,
                nurseId = this[NURSE_ID_KEY],
                phoneNumber = this[PHONE_NUMBER_KEY],
            )
        }
        return AuthenticationState(
            credentials = credentials,
            session = savedSession,
        )
    }

    private fun Preferences.matches(credentials: AuthenticationCredentials): Boolean =
        this[ACCESS_TOKEN_KEY] == credentials.accessToken &&
            this[REFRESH_TOKEN_KEY] == credentials.refreshToken &&
            this[SESSION_ID_KEY] == credentials.sessionId

    private fun MutablePreferences.removeAuthenticationState() {
        remove(ACCESS_TOKEN_KEY)
        remove(REFRESH_TOKEN_KEY)
        remove(SESSION_ID_KEY)
        removeSessionMetadata()
    }

    private fun MutablePreferences.removeSessionMetadata() {
        remove(DESTINATION_KEY)
        remove(NURSE_ID_KEY)
        remove(PHONE_NUMBER_KEY)
    }

    private companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        val SESSION_ID_KEY = stringPreferencesKey("authentication_session_id")
        val DESTINATION_KEY = stringPreferencesKey("authenticated_destination")
        val NURSE_ID_KEY = stringPreferencesKey("authenticated_nurse_id")
        val PHONE_NUMBER_KEY = stringPreferencesKey("authenticated_phone_number")
    }
}
