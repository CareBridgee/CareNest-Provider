package com.carenest.provider.account.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.components.ProviderAccountTopBar
import com.carenest.provider.account.presentation.components.SettingsLoadingSkeleton
import com.carenest.provider.account.presentation.components.SettingsNavigationRow
import com.carenest.provider.account.presentation.components.SettingsSwitchRow
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.bottomsheet.BaseBottomSheet
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenPrivacyPolicy: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showLanguageSelector by rememberSaveable { mutableStateOf(false) }
    val resolvedDarkTheme = Theme.isDarkTheme

    LaunchedEffect(resolvedDarkTheme) {
        viewModel.onIntent(SettingsIntent.ResolvedThemeChanged(resolvedDarkTheme))
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            SettingsEffect.NavigateBack -> onNavigateBack()
            SettingsEffect.OpenLanguage -> showLanguageSelector = true
            SettingsEffect.OpenPrivacyPolicy -> onOpenPrivacyPolicy()
        }
    }
    SettingsContent(state, viewModel::onIntent, modifier)

    if (showLanguageSelector) {
        LanguageSelectionSheet(
            selectedLanguageCode = state.languageCode,
            onSelected = { languageCode ->
                viewModel.onIntent(SettingsIntent.LanguageSelected(languageCode))
                showLanguageSelector = false
            },
            onDismiss = { showLanguageSelector = false },
        )
    }
}

@Composable
fun SettingsContent(
    state: SettingsUiState,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        ProviderAccountTopBar(onNavigateBack = { onIntent(SettingsIntent.BackClicked) })
        if (state.isLoading) {
            SettingsLoadingSkeleton()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
            item {
                BasicText(
                    text = stringResource(R.string.settings_title),
                    style = Theme.typography.title.copy(color = Theme.colors.primaryFont),
                )
                BasicText(
                    text = stringResource(R.string.settings_subtitle),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }
            item {
                SettingsSectionTitle(stringResource(R.string.settings_app_preferences))
                SettingsCard {
                    SettingsNavigationRow(
                        icon = Icons.Rounded.Language,
                        title = stringResource(R.string.settings_language),
                        value = stringResource(
                            if (state.languageCode == "ar") {
                                R.string.settings_language_arabic
                            } else {
                                R.string.settings_language_english
                            },
                        ),
                        onClick = { onIntent(SettingsIntent.LanguageClicked) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SettingsDivider()
                    SettingsSwitchRow(
                        icon = Icons.Rounded.DarkMode,
                        title = stringResource(R.string.settings_dark_mode),
                        checked = state.isDarkModeEnabled,
                        onCheckedChange = { onIntent(SettingsIntent.DarkModeChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            item {
                SettingsSectionTitle(stringResource(R.string.settings_security_privacy))
                SettingsCard {
                    SettingsNavigationRow(
                        icon = Icons.Rounded.PrivacyTip,
                        title = stringResource(R.string.settings_privacy_policy),
                        onClick = { onIntent(SettingsIntent.PrivacyPolicyClicked) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            item { Spacer(Modifier.height(Theme.spacing.extraLarge)) }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LanguageSelectionSheet(
    selectedLanguageCode: String,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(
        title = stringResource(R.string.settings_choose_language),
        onDismissRequest = onDismiss,
    ) {
        LanguageOptionRow(
            label = stringResource(R.string.settings_language_english),
            selected = selectedLanguageCode == "en",
            onClick = { onSelected("en") },
        )
        HorizontalDivider(color = Theme.colors.divider)
        LanguageOptionRow(
            label = stringResource(R.string.settings_language_arabic),
            selected = selectedLanguageCode == "ar",
            onClick = { onSelected("ar") },
        )
        Spacer(Modifier.height(Theme.spacing.medium))
    }
}

@Composable
private fun LanguageOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Theme.spacing.large, vertical = Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Theme.colors.tint,
            )
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    BasicText(
        text = text,
        style = Theme.typography.body.small.copy(
            color = Theme.colors.tint,
            fontWeight = FontWeight.SemiBold,
        ),
        modifier = Modifier.padding(
            start = Theme.spacing.extraSmall,
            bottom = Theme.spacing.small,
        ),
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, Theme.shapes.extraLarge)
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface),
        content = { content() },
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        color = Theme.colors.divider,
        modifier = Modifier.padding(horizontal = Theme.spacing.medium),
    )
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun SettingsLightPreview() {
    SpTheme(isDarkTheme = false) {
        SettingsContent(SettingsUiState(isLoading = false), {})
    }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun SettingsDarkPreview() {
    SpTheme(isDarkTheme = true) {
        SettingsContent(
            SettingsUiState(
                isLoading = false,
                isDarkModeEnabled = true,
            ),
            {},
        )
    }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun SettingsLoadingDarkPreview() {
    SpTheme(isDarkTheme = true) {
        SettingsContent(SettingsUiState(isLoading = true), {})
    }
}
