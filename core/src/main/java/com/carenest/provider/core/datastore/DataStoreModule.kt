package com.carenest.provider.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

private const val TOKEN_DATA_STORE_NAME = "auth_tokens"
private const val APP_PREFERENCES_DATA_STORE_NAME = "app_preferences"
private val Context.tokenDataStore by preferencesDataStore(name = TOKEN_DATA_STORE_NAME)
private val Context.appPreferencesDataStore by preferencesDataStore(
    name = APP_PREFERENCES_DATA_STORE_NAME,
)

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppSettingsDataStore

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenManagerModule {
    @Binds
    @Singleton
    abstract fun bindTokenManager(
        implementation: DataStoreTokenManager
    ): TokenManager

    @Binds
    @Singleton
    abstract fun bindAuthenticationSessionStore(
        implementation: DataStoreAuthenticationSessionStore,
    ): AuthenticationSessionStore
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @AuthDataStore
    @Provides
    @Singleton
    fun provideTokenDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.tokenDataStore

    @AppSettingsDataStore
    @Provides
    @Singleton
    fun provideAppPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.appPreferencesDataStore
}
