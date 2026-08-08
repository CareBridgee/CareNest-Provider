package com.carenest.provider.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
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

interface AuthenticationSessionStore {
    val session: Flow<AuthenticationSession?>
    suspend fun save(session: AuthenticationSession)
    suspend fun clear()
}

class DataStoreAuthenticationSessionStore @Inject constructor(
    @AuthDataStore private val dataStore: DataStore<Preferences>,
) : AuthenticationSessionStore {

    override val session: Flow<AuthenticationSession?> = dataStore.data.map { preferences ->
        val destination = preferences[DESTINATION_KEY]
            ?.let { runCatching { AuthenticationSessionDestination.valueOf(it) }.getOrNull() }
            ?: return@map null
        AuthenticationSession(
            destination = destination,
            nurseId = preferences[NURSE_ID_KEY],
            phoneNumber = preferences[PHONE_NUMBER_KEY],
        )
    }

    override suspend fun save(session: AuthenticationSession) {
        dataStore.edit { preferences ->
            preferences[DESTINATION_KEY] = session.destination.name
            session.nurseId?.let { preferences[NURSE_ID_KEY] = it }
                ?: preferences.remove(NURSE_ID_KEY)
            session.phoneNumber?.let { preferences[PHONE_NUMBER_KEY] = it }
                ?: preferences.remove(PHONE_NUMBER_KEY)
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(DESTINATION_KEY)
            preferences.remove(NURSE_ID_KEY)
            preferences.remove(PHONE_NUMBER_KEY)
        }
    }

    private companion object {
        val DESTINATION_KEY = stringPreferencesKey("authenticated_destination")
        val NURSE_ID_KEY = stringPreferencesKey("authenticated_nurse_id")
        val PHONE_NUMBER_KEY = stringPreferencesKey("authenticated_phone_number")
    }
}
