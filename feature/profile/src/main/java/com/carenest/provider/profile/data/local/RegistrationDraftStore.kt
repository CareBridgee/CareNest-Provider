package com.carenest.provider.profile.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.IOException
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val Context.registrationDraftDataStore by preferencesDataStore(
    name = "registration_draft",
)

@Serializable
data class AttachmentDraft(
    val uri: String,
    val name: String,
    val mimeType: String,
)

@Serializable
data class RegistrationDraft(
    val currentPage: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val nationalId: String = "",
    val gender: String = "UNKNOWN",
    val profilePhoto: AttachmentDraft? = null,
    val nationalIdFront: AttachmentDraft? = null,
    val nationalIdBack: AttachmentDraft? = null,
    val licenseNumber: String = "",
    val nursingLicense: AttachmentDraft? = null,
    val professionalCertificate: AttachmentDraft? = null,
    val yearsOfExp: Int? = null,
    val primarySpeciality: String = "",
    val selectedServiceIds: List<String> = emptyList(),
    val isCertified: Boolean = false,
)

interface RegistrationDraftStore {
    val draft: Flow<RegistrationDraft?>
    suspend fun save(draft: RegistrationDraft)
    suspend fun clear()
}

class DataStoreRegistrationDraftStore @Inject constructor(
    @param:RegistrationDraftDataStore private val dataStore: DataStore<Preferences>,
    private val json: Json,
) : RegistrationDraftStore {

    override val draft: Flow<RegistrationDraft?> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map { preferences ->
            preferences[DRAFT_KEY]?.let { encoded ->
                runCatching { json.decodeFromString<RegistrationDraft>(encoded) }.getOrNull()
            }
        }

    override suspend fun save(draft: RegistrationDraft) {
        dataStore.edit { preferences ->
            preferences[DRAFT_KEY] = json.encodeToString(draft)
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    private companion object {
        val DRAFT_KEY = stringPreferencesKey("draft")
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RegistrationDraftDataStore

@Module
@InstallIn(SingletonComponent::class)
object RegistrationDraftDataStoreModule {
    @Provides
    @Singleton
    @RegistrationDraftDataStore
    fun provideRegistrationDraftDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.registrationDraftDataStore

    @Provides
    @Singleton
    fun provideRegistrationDraftStore(
        implementation: DataStoreRegistrationDraftStore,
    ): RegistrationDraftStore = implementation
}
