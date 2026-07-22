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
private val Context.tokenDataStore by preferencesDataStore(name = TOKEN_DATA_STORE_NAME)

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthDataStore

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenManagerModule {
    @Binds
    @Singleton
    abstract fun bindTokenManager(
        implementation: DataStoreTokenManager
    ): TokenManager
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
}
