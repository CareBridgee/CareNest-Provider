package com.carenest.provider.account.presentation.profilemenu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.components.ProfileMenuLoadingSkeleton
import com.carenest.provider.account.presentation.components.ProfileMenuCard
import com.carenest.provider.account.presentation.components.ProfileMenuHero
import com.carenest.provider.account.presentation.components.ProviderAccountTopBar
import com.carenest.provider.account.presentation.model.MenuItemId
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun ProfileMenuRoute(
    onOpenPublicProfile: () -> Unit,
    onOpenDocuments: () -> Unit,
    onOpenRatingsAndReviews: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenEarnings: () -> Unit,
    onOpenPayouts: () -> Unit,
    onOpenSupport: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileMenuViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            ProfileMenuEffect.OpenPublicProfile -> onOpenPublicProfile()
            ProfileMenuEffect.OpenDocuments -> onOpenDocuments()
            ProfileMenuEffect.OpenRatingsAndReviews -> onOpenRatingsAndReviews()
            ProfileMenuEffect.OpenSettings -> onOpenSettings()
            ProfileMenuEffect.OpenEarnings -> onOpenEarnings()
            ProfileMenuEffect.OpenPayouts -> onOpenPayouts()
            ProfileMenuEffect.OpenSupport -> onOpenSupport()
            ProfileMenuEffect.Logout -> onLogout()
        }
    }
    ProfileMenuContent(state, viewModel::onIntent, modifier)
}

@Composable
fun ProfileMenuContent(
    state: ProfileMenuUiState,
    onIntent: (ProfileMenuIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        ProviderAccountTopBar(
            title = stringResource(R.string.account_provider_short_name),
            showSettings = true,
            onSettingsClick = { onIntent(ProfileMenuIntent.SettingsClicked) },
        )
        if (state.isLoading) {
            ProfileMenuLoadingSkeleton()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
            item {
                ProfileMenuHero(
                    name = stringResource(R.string.account_provider_name),
                    specialty = stringResource(R.string.account_specialty),
                    rating = stringResource(R.string.account_rating_value),
                    onClick = { onIntent(ProfileMenuIntent.ProfileCardClicked) },
                )
            }
            items(state.menuItems, key = { it.id }) { item ->
                ProfileMenuCard(
                    title = stringResource(item.titleRes),
                    subtitle = stringResource(item.subtitleRes),
                    iconRes = item.iconRes,
                    showVerifiedDot = item.showVerifiedDot,
                    onClick = {
                        onIntent(
                            when (item.id) {
                                MenuItemId.Earnings -> ProfileMenuIntent.EarningsClicked
                                MenuItemId.Payouts -> ProfileMenuIntent.PayoutsClicked
                                MenuItemId.Availability ->
                                    ProfileMenuIntent.AvailabilitySettingsClicked
                                else -> ProfileMenuIntent.MenuItemClicked(item.id)
                            },
                        )
                    },
                )
            }
            item {
                Spacer(Modifier.height(Theme.spacing.small))
                OutlinedButton(
                    onClick = { onIntent(ProfileMenuIntent.LogoutClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, Theme.colors.error.copy(alpha = .25f), RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Theme.colors.error),
                    border = null,
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(Theme.spacing.extraSmall))
                    BasicText(
                        text = stringResource(R.string.profile_menu_logout),
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.error,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
            item {
                BasicText(
                    text = stringResource(R.string.profile_menu_version),
                    style = Theme.typography.hint.large.copy(
                        color = Theme.colors.hint,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = Theme.spacing.small,
                            bottom = Theme.spacing.medium,
                        ),
                )
            }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ProfileMenuLightPreview() {
    SpTheme(isDarkTheme = false) {
        ProfileMenuContent(ProfileMenuUiState(), {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ProfileMenuDarkPreview() {
    SpTheme(isDarkTheme = true) {
        ProfileMenuContent(ProfileMenuUiState(), {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ProfileMenuLoadingPreview() {
    SpTheme(isDarkTheme = false) {
        ProfileMenuContent(ProfileMenuUiState(isLoading = true), {})
    }
}
