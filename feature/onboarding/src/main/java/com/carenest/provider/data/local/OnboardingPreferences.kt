package com.carenest.provider.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.carenest.provider.feature.onboarding.di.OnboardingDataStore
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.text.get

interface OnboardingPreferences {
    val isCompleted: Flow<Boolean>
    suspend fun setCompleted()
}

class DataStoreOnboardingPreferences @Inject constructor(
    @param:OnboardingDataStore private val dataStore: DataStore<Preferences>,
) : OnboardingPreferences {

    override val isCompleted: Flow<Boolean> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            preferences[PreferenceKeys.IS_PROVIDER_ONBOARDING_COMPLETED] ?: false
        }

    override suspend fun setCompleted() {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_PROVIDER_ONBOARDING_COMPLETED] = true
        }
    }

    internal object PreferenceKeys {
        val IS_PROVIDER_ONBOARDING_COMPLETED =
            booleanPreferencesKey("IS_PROVIDER_ONBOARDING_COMPLETED")
    }
}
