package com.carenest.provider.feature.onboarding.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.carenest.provider.data.local.DataStoreOnboardingPreferences
import com.carenest.provider.data.local.OnboardingPreferences
import com.carenest.provider.feature.onboarding.data.repository.OnboardingRepositoryImpl
import com.carenest.provider.feature.onboarding.domain.repository.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

private const val PROVIDER_ONBOARDING_DATA_STORE_NAME = "provider_onboarding"
private val Context.providerOnboardingDataStore by preferencesDataStore(
    name = PROVIDER_ONBOARDING_DATA_STORE_NAME,
)

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OnboardingDataStore

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingBindingsModule {
    @Binds
    @Singleton
    abstract fun bindPreferences(
        implementation: DataStoreOnboardingPreferences,
    ): OnboardingPreferences

    @Binds
    @Singleton
    abstract fun bindRepository(
        implementation: OnboardingRepositoryImpl,
    ): OnboardingRepository
}

@Module
@InstallIn(SingletonComponent::class)
object OnboardingDataStoreModule {
    @OnboardingDataStore
    @Provides
    @Singleton
    fun provideOnboardingDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.providerOnboardingDataStore
}
