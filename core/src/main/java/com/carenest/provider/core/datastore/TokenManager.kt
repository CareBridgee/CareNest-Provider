package com.carenest.provider.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface TokenManager {
    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>
    suspend fun saveTokens(accessToken: String?, refreshToken: String?)
    suspend fun clearTokens()
}

class DataStoreTokenManager @Inject constructor(
    @AuthDataStore private val dataStore: DataStore<Preferences>
) : TokenManager {

    override val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    override val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    override suspend fun saveTokens(accessToken: String?, refreshToken: String?) {
        dataStore.edit { preferences ->
            if (accessToken != null) preferences[ACCESS_TOKEN_KEY] = accessToken
            if (refreshToken != null) preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}
