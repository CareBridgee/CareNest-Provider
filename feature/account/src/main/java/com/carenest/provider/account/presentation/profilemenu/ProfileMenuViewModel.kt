package com.carenest.provider.account.presentation.profilemenu

import androidx.lifecycle.ViewModel
import com.carenest.provider.account.presentation.model.MenuItemId
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileMenuViewModel @Inject constructor() : ViewModel(),
    StateHolder<ProfileMenuUiState> by DefaultStateHolder(ProfileMenuUiState()),
    EffectPublisher<ProfileMenuEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: ProfileMenuIntent) {
        when (intent) {
            ProfileMenuIntent.ProfileCardClicked ->
                sendEffect(ProfileMenuEffect.OpenPublicProfile)
            ProfileMenuIntent.SettingsClicked -> sendEffect(ProfileMenuEffect.OpenSettings)
            ProfileMenuIntent.AvailabilitySettingsClicked ->
                sendEffect(ProfileMenuEffect.OpenSettings)
            ProfileMenuIntent.EarningsClicked -> sendEffect(ProfileMenuEffect.OpenEarnings)
            ProfileMenuIntent.PayoutsClicked -> sendEffect(ProfileMenuEffect.OpenPayouts)
            ProfileMenuIntent.LogoutClicked -> sendEffect(ProfileMenuEffect.Logout)
            is ProfileMenuIntent.MenuItemClicked -> when (intent.id) {
                MenuItemId.ProfessionalInfo -> sendEffect(ProfileMenuEffect.OpenPublicProfile)
                MenuItemId.Documents -> sendEffect(ProfileMenuEffect.OpenDocuments)
                MenuItemId.Availability -> sendEffect(ProfileMenuEffect.OpenSettings)
                MenuItemId.Reviews -> sendEffect(ProfileMenuEffect.OpenRatingsAndReviews)
                MenuItemId.Earnings -> sendEffect(ProfileMenuEffect.OpenEarnings)
                MenuItemId.Payouts -> sendEffect(ProfileMenuEffect.OpenPayouts)
                MenuItemId.Support -> sendEffect(ProfileMenuEffect.OpenSupport)
            }
        }
    }
}
