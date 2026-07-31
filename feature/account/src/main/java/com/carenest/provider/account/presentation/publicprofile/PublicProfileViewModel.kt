package com.carenest.provider.account.presentation.publicprofile

import androidx.lifecycle.ViewModel
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PublicProfileViewModel @Inject constructor() : ViewModel(),
    StateHolder<PublicProfileUiState> by DefaultStateHolder(PublicProfileUiState()),
    EffectPublisher<PublicProfileEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: PublicProfileIntent) {
        sendEffect(
            when (intent) {
                PublicProfileIntent.BackClicked -> PublicProfileEffect.NavigateBack
                PublicProfileIntent.EditAddressClicked -> PublicProfileEffect.EditAddress
                PublicProfileIntent.ShareProfileClicked -> PublicProfileEffect.ShareProfile
                PublicProfileIntent.SettingsClicked -> PublicProfileEffect.OpenSettings
            },
        )
    }
}
