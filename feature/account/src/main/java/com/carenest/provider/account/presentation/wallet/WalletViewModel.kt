package com.carenest.provider.account.presentation.wallet

import androidx.lifecycle.ViewModel
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor() : ViewModel(),
    StateHolder<WalletUiState> by DefaultStateHolder(WalletUiState()),
    EffectPublisher<WalletEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: WalletIntent) {
        when (intent) {
            WalletIntent.BackClicked -> sendEffect(WalletEffect.NavigateBack)
            WalletIntent.ManagePrimaryClicked -> sendEffect(WalletEffect.ManagePrimary)
            WalletIntent.AddNewMethodClicked -> sendEffect(WalletEffect.AddNewMethod)
            WalletIntent.ViewAllClicked -> sendEffect(WalletEffect.ViewAllMethods)
            is WalletIntent.AlternativeMethodClicked ->
                sendEffect(WalletEffect.OpenAlternativeMethod(intent.id))
        }
    }
}
