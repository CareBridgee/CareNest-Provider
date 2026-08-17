package com.carenest.provider.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

enum class AppThemeMode {
    System,
    Light,
    Dark,
}

data class AppPreferencesState(
    val themeMode: AppThemeMode = AppThemeMode.System,
    val languageCode: String = defaultLanguageCode(),
    val isOnline: Boolean = false,
)

interface AppPreferences {
    val state: Flow<AppPreferencesState>

    suspend fun setThemeMode(mode: AppThemeMode)

    suspend fun setLanguageCode(languageCode: String)

    suspend fun setOnline(isOnline: Boolean)

    suspend fun clear()
}

class DataStoreAppPreferences @Inject constructor(
    @param:AppSettingsDataStore private val dataStore: DataStore<Preferences>,
) : AppPreferences {

    override val state: Flow<AppPreferencesState> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            AppPreferencesState(
                themeMode = preferences[Keys.THEME_MODE]
                    ?.let { saved -> AppThemeMode.entries.firstOrNull { it.name == saved } }
                    ?: AppThemeMode.System,
                languageCode = preferences[Keys.LANGUAGE_CODE]
                    ?.takeIf { it in SUPPORTED_LANGUAGE_CODES }
                    ?: defaultLanguageCode(),
                isOnline = preferences[Keys.IS_ONLINE] ?: false,
            )
        }
        .distinctUntilChanged()

    override suspend fun setThemeMode(mode: AppThemeMode) {
        dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = mode.name
        }
    }

    override suspend fun setLanguageCode(languageCode: String) {
        require(languageCode in SUPPORTED_LANGUAGE_CODES)
        dataStore.edit { preferences ->
            preferences[Keys.LANGUAGE_CODE] = languageCode
        }
    }

    override suspend fun setOnline(isOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_ONLINE] = isOnline
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("app_theme_mode")
        val LANGUAGE_CODE = stringPreferencesKey("app_language_code")
        val IS_ONLINE = booleanPreferencesKey("is_online")
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppPreferencesModule {
    @Binds
    @Singleton
    abstract fun bindAppPreferences(
        implementation: DataStoreAppPreferences,
    ): AppPreferences
}

private val SUPPORTED_LANGUAGE_CODES = setOf("en", "ar")

private fun defaultLanguageCode(): String =
    Locale.getDefault().language.takeIf { it in SUPPORTED_LANGUAGE_CODES } ?: "en"
