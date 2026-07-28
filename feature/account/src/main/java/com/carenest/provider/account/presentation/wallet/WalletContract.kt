package com.carenest.provider.account.presentation.wallet

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.carenest.provider.account.R
import com.carenest.provider.designsystem.R as DesignSystemR

data class WalletUiState(
    val isLoading: Boolean = false,
    val primaryMethod: PrimaryPayoutMethodUiModel = PrimaryPayoutMethodUiModel(),
    val alternativeMethods: List<AlternativePayoutMethodUiModel> = sampleAlternativeMethods,
)

data class PrimaryPayoutMethodUiModel(
    val isVerified: Boolean = true,
)

data class AlternativePayoutMethodUiModel(
    val id: String,
    @param:StringRes val titleRes: Int,
    @param:StringRes val subtitleRes: Int,
    @param:DrawableRes val iconRes: Int,
    val accent: AlternativePayoutAccent,
)

enum class AlternativePayoutAccent {
    Vodafone,
    Neutral,
}

sealed interface WalletIntent {
    data object BackClicked : WalletIntent
    data object ManagePrimaryClicked : WalletIntent
    data object AddNewMethodClicked : WalletIntent
    data object ViewAllClicked : WalletIntent
    data class AlternativeMethodClicked(val id: String) : WalletIntent
}

sealed interface WalletEffect {
    data object NavigateBack : WalletEffect
    data object ManagePrimary : WalletEffect
    data object AddNewMethod : WalletEffect
    data object ViewAllMethods : WalletEffect
    data class OpenAlternativeMethod(val id: String) : WalletEffect
}

private val sampleAlternativeMethods = listOf(
    AlternativePayoutMethodUiModel(
        id = "vodafone-cash",
        titleRes = R.string.wallet_vodafone_cash,
        subtitleRes = R.string.wallet_vodafone_number,
        iconRes = DesignSystemR.drawable.ic_wallet,
        accent = AlternativePayoutAccent.Vodafone,
    ),
    AlternativePayoutMethodUiModel(
        id = "instapay",
        titleRes = R.string.wallet_instapay,
        subtitleRes = R.string.wallet_instapay_account,
        iconRes = DesignSystemR.drawable.ic_payment_method,
        accent = AlternativePayoutAccent.Neutral,
    ),
)
