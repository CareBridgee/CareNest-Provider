package com.carenest.provider.account.presentation.publicprofile

data class PublicProfileUiState(
    val isLoading: Boolean = false,
    val isVerified: Boolean = true,
)

sealed interface PublicProfileIntent {
    data object BackClicked : PublicProfileIntent
    data object EditAddressClicked : PublicProfileIntent
    data object ShareProfileClicked : PublicProfileIntent
    data object SettingsClicked : PublicProfileIntent
}

sealed interface PublicProfileEffect {
    data object NavigateBack : PublicProfileEffect
    data object EditAddress : PublicProfileEffect
    data object ShareProfile : PublicProfileEffect
    data object OpenSettings : PublicProfileEffect
}
