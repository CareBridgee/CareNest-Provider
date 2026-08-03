package com.carenest.provider.account.presentation.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.components.AlternativePayoutMethodCard
import com.carenest.provider.account.presentation.components.PrimaryPayoutCard
import com.carenest.provider.account.presentation.components.ProviderAccountTopBar
import com.carenest.provider.account.presentation.components.WalletLoadingSkeleton
import com.carenest.provider.account.presentation.components.WalletSecureBadge
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.R as DesignSystemR

@Composable
fun WalletRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onManagePrimary: () -> Unit = {},
    onAddNewMethod: () -> Unit = {},
    onViewAllMethods: () -> Unit = {},
    onOpenAlternativeMethod: (String) -> Unit = {},
    viewModel: WalletViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            WalletEffect.NavigateBack -> onNavigateBack()
            WalletEffect.ManagePrimary -> onManagePrimary()
            WalletEffect.AddNewMethod -> onAddNewMethod()
            WalletEffect.ViewAllMethods -> onViewAllMethods()
            is WalletEffect.OpenAlternativeMethod -> onOpenAlternativeMethod(effect.id)
        }
    }
    WalletContent(state = state, onIntent = viewModel::onIntent, modifier = modifier)
}

@Composable
fun WalletContent(
    state: WalletUiState,
    onIntent: (WalletIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        ProviderAccountTopBar(onNavigateBack = { onIntent(WalletIntent.BackClicked) })
        if (state.isLoading) {
            WalletLoadingSkeleton()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
            ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        BasicText(
                            text = stringResource(R.string.wallet_title),
                            style = Theme.typography.title.copy(
                                color = Theme.colors.primaryFont,
                            ),
                        )
                        BasicText(
                            text = stringResource(R.string.wallet_subtitle),
                            style = Theme.typography.body.small.copy(
                                color = Theme.colors.secondaryFont,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    }
                    WalletSecureBadge(stringResource(R.string.wallet_secure))
                }
            }
            item {
                WalletSectionHeader(stringResource(R.string.wallet_primary_method))
                PrimaryPayoutCard(
                    method = state.primaryMethod,
                    title = stringResource(R.string.wallet_linked_bank),
                    subtitle = stringResource(R.string.wallet_standard_payout),
                    accountNumberLabel = stringResource(R.string.wallet_account_number),
                    maskedAccountNumber = stringResource(R.string.wallet_masked_account),
                    statusLabel = stringResource(R.string.wallet_status),
                    verifiedLabel = stringResource(R.string.wallet_verified),
                    manageLabel = stringResource(R.string.wallet_manage_primary),
                    addMethodLabel = stringResource(R.string.wallet_add_method),
                    onManageClick = { onIntent(WalletIntent.ManagePrimaryClicked) },
                    onAddMethodClick = { onIntent(WalletIntent.AddNewMethodClicked) },
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Theme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    WalletSectionHeader(
                        text = stringResource(R.string.wallet_alternative_methods),
                        modifier = Modifier.weight(1f),
                    )
                    androidx.compose.foundation.text.BasicText(
                        text = stringResource(R.string.wallet_view_all),
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.tint,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier
                            .clip(Theme.shapes.small)
                            .clickable { onIntent(WalletIntent.ViewAllClicked) }
                            .padding(Theme.spacing.extraSmall),
                    )
                }
            }
            items(state.alternativeMethods, key = { it.id }) { method ->
                AlternativePayoutMethodCard(
                    method = method,
                    title = stringResource(method.titleRes),
                    subtitle = stringResource(method.subtitleRes),
                    onClick = { onIntent(WalletIntent.AlternativeMethodClicked(method.id)) },
                )
            }
            item { AutomaticPayoutsCard() }
            }
        }
    }
}

@Composable
private fun WalletSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    BasicText(
        text = text,
        style = Theme.typography.hint.large.copy(
            color = Theme.colors.secondaryFont,
            fontWeight = FontWeight.SemiBold,
        ),
        modifier = modifier.padding(bottom = Theme.spacing.small),
    )
}

@Composable
private fun AutomaticPayoutsCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.infoContainer)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_account_info),
            contentDescription = null,
            tint = Color.Unspecified,
        )
        Spacer(Modifier.padding(Theme.spacing.small))
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
            BasicText(
                text = stringResource(R.string.wallet_automatic_payouts),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.tint,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            BasicText(
                text = stringResource(R.string.wallet_automatic_payouts_body),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Normal,
                ),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun WalletLightPreview() {
    SpTheme(isDarkTheme = false) {
        WalletContent(WalletUiState(), {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun WalletDarkPreview() {
    SpTheme(isDarkTheme = true) {
        WalletContent(WalletUiState(), {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun WalletLoadingPreview() {
    SpTheme(isDarkTheme = false) {
        WalletContent(WalletUiState(isLoading = true), {})
    }
}
