package com.carenest.provider.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthenticationSessionStoreTest {

    private val dataStore: DataStore<Preferences> = InMemoryPreferencesDataStore()
    private val store = DataStoreAuthenticationSessionStore(dataStore)

    @Test
    fun authenticationIsPublishedOnlyAfterCredentialsAndProviderSessionExist() = runBlocking {
        store.beginAuthentication("access-a", "refresh-a")

        val pending = store.state.first()
        assertFalse(pending.isAuthenticated)
        assertEquals("access-a", pending.credentials?.accessToken)
        assertEquals("refresh-a", pending.credentials?.refreshToken)
        assertNull(pending.session)

        val credentials = requireNotNull(pending.credentials)
        assertTrue(
            store.completeAuthentication(
                expectedCredentials = credentials,
                session = AuthenticationSession(
                    destination = AuthenticationSessionDestination.APPROVED,
                    nurseId = "nurse-a",
                    phoneNumber = "+201000000001",
                ),
            ),
        )

        val authenticated = store.state.first()
        assertTrue(authenticated.isAuthenticated)
        assertEquals("nurse-a", authenticated.session?.nurseId)
    }

    @Test
    fun staleAccountCannotRotateOrClearNewAccountSession() = runBlocking {
        store.beginAuthentication("access-a", "refresh-a")
        val initialAccountACredentials = requireNotNull(store.state.first().credentials)
        store.completeAuthentication(
            expectedCredentials = initialAccountACredentials,
            session = AuthenticationSession(
                destination = AuthenticationSessionDestination.APPROVED,
                nurseId = "nurse-a",
                phoneNumber = "+201000000001",
            ),
        )
        val accountACredentials = requireNotNull(store.state.first().credentials)

        store.beginAuthentication("access-b", "refresh-b")
        val accountBCredentials = requireNotNull(store.state.first().credentials)
        assertFalse(
            store.completeAuthentication(
                expectedCredentials = accountACredentials,
                session = AuthenticationSession(
                    destination = AuthenticationSessionDestination.APPROVED,
                    nurseId = "stale-nurse-a",
                    phoneNumber = "+201000000001",
                ),
            ),
        )
        store.completeAuthentication(
            expectedCredentials = accountBCredentials,
            session = AuthenticationSession(
                destination = AuthenticationSessionDestination.APPROVED,
                nurseId = "nurse-b",
                phoneNumber = "+201000000002",
            ),
        )

        assertFalse(
            store.replaceCredentials(
                expectedCredentials = accountACredentials,
                accessToken = "stale-access-a",
                refreshToken = "stale-refresh-a",
            ),
        )
        assertFalse(store.clearSessionIfCurrent(accountACredentials))

        val accountB = store.state.first()
        assertTrue(accountB.isAuthenticated)
        assertEquals("access-b", accountB.credentials?.accessToken)
        assertEquals("refresh-b", accountB.credentials?.refreshToken)
        assertEquals("nurse-b", accountB.session?.nurseId)
    }

    @Test
    fun clearSessionRemovesTokensAndAllProviderMetadata() = runBlocking {
        store.beginAuthentication("access-a", "refresh-a")
        val credentials = requireNotNull(store.state.first().credentials)
        store.completeAuthentication(
            expectedCredentials = credentials,
            session = AuthenticationSession(
                destination = AuthenticationSessionDestination.REJECTED,
                nurseId = "nurse-a",
                phoneNumber = "+201000000001",
            ),
        )

        store.clearSession()

        val cleared = store.state.first()
        assertFalse(cleared.isAuthenticated)
        assertNull(cleared.credentials)
        assertNull(cleared.session)
    }

    @Test
    fun invalidLegacyStateWithMissingRefreshTokenIsRemoved() = runBlocking {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("access_token")] = "orphan-access"
            preferences[stringPreferencesKey("authenticated_destination")] =
                AuthenticationSessionDestination.APPROVED.name
            preferences[stringPreferencesKey("authenticated_nurse_id")] = "old-nurse"
        }

        assertTrue(store.clearInvalidSession())

        val cleared = store.state.first()
        assertNull(cleared.credentials)
        assertNull(cleared.session)
    }

    private class InMemoryPreferencesDataStore : DataStore<Preferences> {
        private val mutex = Mutex()
        private val mutableData = MutableStateFlow<Preferences>(emptyPreferences())

        override val data = mutableData

        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
            mutex.withLock {
                transform(mutableData.value).also { mutableData.value = it }
            }
    }
}
