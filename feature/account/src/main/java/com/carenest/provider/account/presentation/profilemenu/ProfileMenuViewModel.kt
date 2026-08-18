package com.carenest.provider.account.presentation.profilemenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.account.presentation.model.MenuItemId
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import com.carenest.provider.core.datastore.AppPreferences
import com.carenest.provider.profile.data.local.RegistrationDraftStore
import kotlinx.coroutines.launch
import com.carenest.provider.profile.domain.usecase.GetNurseUseCase

@HiltViewModel
class ProfileMenuViewModel @Inject constructor(
    private val authenticationSessionStore: AuthenticationSessionStore,
    private val appPreferences: AppPreferences,
    private val registrationDraftStore: RegistrationDraftStore,
    private val getNurse: GetNurseUseCase,
) : ViewModel(),
    StateHolder<ProfileMenuUiState> by DefaultStateHolder(
        ProfileMenuUiState(
            isLoading = true,
            avatarUrl = authenticationSessionStore.currentSession?.profileImageUrl,
        ),
    ),
    EffectPublisher<ProfileMenuEffect> by DefaultEffectPublisher() {

    private var profileJob: Job? = null

    init {
        loadProfile()
    }

    fun onIntent(intent: ProfileMenuIntent) {
        when (intent) {
            ProfileMenuIntent.ProfileCardClicked ->
                sendEffect(ProfileMenuEffect.OpenPublicProfile)
            ProfileMenuIntent.SettingsClicked -> sendEffect(ProfileMenuEffect.OpenSettings)
            ProfileMenuIntent.EarningsClicked -> sendEffect(ProfileMenuEffect.OpenEarnings)
            ProfileMenuIntent.PayoutsClicked -> sendEffect(ProfileMenuEffect.OpenPayouts)
            ProfileMenuIntent.WalletClicked -> sendEffect(ProfileMenuEffect.OpenWallet)
            ProfileMenuIntent.LogoutClicked -> viewModelScope.launch {
                authenticationSessionStore.clearSession()
                appPreferences.clear()
                registrationDraftStore.clear()
                sendEffect(ProfileMenuEffect.Logout)
            }
            ProfileMenuIntent.RefreshProfile -> loadProfile()
            is ProfileMenuIntent.MenuItemClicked -> when (intent.id) {
                MenuItemId.ProfessionalInfo -> sendEffect(ProfileMenuEffect.OpenPublicProfile)
                MenuItemId.Documents -> sendEffect(ProfileMenuEffect.OpenDocuments)
                MenuItemId.Settings -> sendEffect(ProfileMenuEffect.OpenSettings)
                MenuItemId.Reviews -> sendEffect(ProfileMenuEffect.OpenRatingsAndReviews)
                MenuItemId.Earnings -> sendEffect(ProfileMenuEffect.OpenEarnings)
            }
        }
    }

    private fun loadProfile() {
        profileJob?.cancel()
        profileJob = viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val savedSession = authenticationSessionStore.currentSession
                ?: authenticationSessionStore.session.first()
            val nurseId = savedSession?.nurseId
            if (nurseId.isNullOrBlank()) {
                updateState { copy(isLoading = false) }
                return@launch
            }
            savedSession.profileImageUrl?.takeIf(String::isNotBlank)?.let { cachedUrl ->
                updateState { copy(avatarUrl = cachedUrl) }
            }
            getNurse(nurseId).fold(
                onSuccess = { profile ->
                    authenticationSessionStore.updateProfileImageUrl(
                        nurseId = nurseId,
                        profileImageUrl = profile.profileImageUrl,
                    )
                    val fullName = listOfNotNull(
                        profile.firstName?.takeIf(String::isNotBlank),
                        profile.lastName?.takeIf(String::isNotBlank),
                    ).joinToString(" ")
                    updateState {
                        copy(
                            isLoading = false,
                            fullName = fullName,
                            avatarUrl = profile.profileImageUrl,
                            specialty = profile.specialization.orEmpty(),
                            rating = profile.ratingAvg?.let { "%.1f".format(it) } ?: "0",
                            reviewCount = profile.totalReviews ?: 0,
                            menuItems = menuItems.map { item ->
                                if (item.id == MenuItemId.Reviews) {
                                    val count = profile.totalReviews ?: 0
                                    item.copy(subtitle = "$count patient testimonials")
                                } else item
                            }
                        )
                    }
                },
                onFailure = { updateState { copy(isLoading = false) } },
            )
        }
    }
}
