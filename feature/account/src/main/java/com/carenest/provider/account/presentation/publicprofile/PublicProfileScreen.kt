package com.carenest.provider.account.presentation.publicprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.components.InitialsAvatar
import com.carenest.provider.account.presentation.components.ProviderAccountTopBar
import com.carenest.provider.account.presentation.components.ProviderStatisticCard
import com.carenest.provider.account.presentation.components.PublicProfileLoadingSkeleton
import com.carenest.provider.account.presentation.components.RatingStars
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.R as DesignSystemR
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun PublicProfileRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onEditAddress: () -> Unit = {},
    onShareProfile: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    viewModel: PublicProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            PublicProfileEffect.NavigateBack -> onNavigateBack()
            PublicProfileEffect.EditAddress -> onEditAddress()
            PublicProfileEffect.ShareProfile -> onShareProfile()
            PublicProfileEffect.OpenSettings -> onOpenSettings()
        }
    }
    PublicProfileContent(state, viewModel::onIntent, modifier)
}

@Composable
fun PublicProfileContent(
    state: PublicProfileUiState,
    onIntent: (PublicProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        ProviderAccountTopBar(
            showSettings = true,
            onSettingsClick = { onIntent(PublicProfileIntent.SettingsClicked) },
            onNavigateBack = { onIntent(PublicProfileIntent.BackClicked) },
        )
        if (state.isLoading) {
            PublicProfileLoadingSkeleton()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
            ) {
                item { PublicProfileHero(state) }
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        ProfileActionButton(
                            label = stringResource(R.string.public_profile_edit_address),
                            icon = Icons.Rounded.Edit,
                            primary = true,
                            onClick = { onIntent(PublicProfileIntent.EditAddressClicked) },
                            modifier = Modifier.weight(1f),
                        )
                        ProfileActionButton(
                            label = stringResource(R.string.public_profile_share),
                            icon = Icons.Rounded.Share,
                            primary = false,
                            onClick = { onIntent(PublicProfileIntent.ShareProfileClicked) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                item { AboutMeCard() }
                item { FeaturedReviewCard() }
            }
        }
    }
}

@Composable
private fun PublicProfileHero(state: PublicProfileUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.primaryContainer)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Image(
                painter = painterResource(DesignSystemR.drawable.nurse_image),
                contentDescription = stringResource(R.string.account_profile_photo),
                modifier = Modifier
                    .size(116.dp)
                    .clip(CircleShape)
                    .border(5.dp, Theme.colors.surface, CircleShape),
            )
            if (state.isVerified) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(40.dp)
                        .shadow(2.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Theme.colors.surface)
                        .padding(5.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Theme.colors.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = stringResource(R.string.documents_verified),
                            tint = Theme.colors.onPrimary,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(Theme.spacing.medium))
        BasicText(
            text = stringResource(R.string.account_provider_name),
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                textAlign = TextAlign.Center,
            ),
        )
        Spacer(Modifier.height(Theme.spacing.small))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_services),
                contentDescription = null,
                tint = Theme.colors.tint,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(Theme.spacing.small))
            BasicText(
                text = stringResource(R.string.account_specialty),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.tint,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
        Spacer(Modifier.height(Theme.spacing.medium))
        Row(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            modifier = Modifier.fillMaxWidth(),
        ) {
            ProviderStatisticCard(
                stringResource(R.string.account_rating_value),
                stringResource(R.string.public_profile_reviews),
                Modifier.weight(1f),
            )
            ProviderStatisticCard(
                stringResource(R.string.public_profile_experience_value),
                stringResource(R.string.public_profile_experience),
                Modifier.weight(1f),
            )
            ProviderStatisticCard(
                stringResource(R.string.public_profile_visits_value),
                stringResource(R.string.public_profile_visits),
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProfileActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = Theme.shapes.large,
        contentPadding = PaddingValues(horizontal = Theme.spacing.small),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) Theme.colors.primary else Theme.colors.primaryContainer,
            contentColor = if (primary) Theme.colors.onPrimary else Theme.colors.secondaryFont,
        ),
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(Theme.spacing.small))
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = if (primary) Theme.colors.onPrimary else Theme.colors.secondaryFont,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun AboutMeCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, Theme.shapes.extraLarge)
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Person, contentDescription = null, tint = Theme.colors.tint)
            Spacer(Modifier.width(Theme.spacing.small))
            BasicText(
                text = stringResource(R.string.public_profile_about),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
        HorizontalDivider(color = Theme.colors.divider)
        BasicText(
            text = stringResource(R.string.public_profile_bio),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp,
            ),
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        ) {
            listOf(
                R.string.public_profile_skill_recovery,
                R.string.public_profile_skill_medication,
                R.string.public_profile_skill_wound,
            ).forEach { label ->
                BasicText(
                    text = stringResource(label),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Normal,
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Theme.colors.primaryContainer)
                        .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.extraSmall),
                )
            }
        }
    }
}

@Composable
private fun FeaturedReviewCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, Theme.shapes.extraLarge)
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface),
    ) {
        Spacer(
            Modifier
                .width(5.dp)
                .height(160.dp)
                .background(Theme.colors.tint),
        )
        Column(
            Modifier.padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                InitialsAvatar("JD")
                Spacer(Modifier.width(Theme.spacing.medium))
                Column(Modifier.weight(1f)) {
                    BasicText(
                        text = stringResource(R.string.public_profile_review_author),
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    BasicText(
                        text = stringResource(R.string.public_profile_review_date),
                        style = Theme.typography.hint.large.copy(
                            color = Theme.colors.hint,
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                }
                RatingStars(rating = 5, iconSize = 18)
            }
            BasicText(
                text = stringResource(R.string.public_profile_review_quote),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 22.sp,
                ),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun PublicProfileLightPreview() {
    SpTheme(isDarkTheme = false) {
        PublicProfileContent(PublicProfileUiState(), {})
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun PublicProfileDarkPreview() {
    SpTheme(isDarkTheme = true) {
        PublicProfileContent(PublicProfileUiState(), {})
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun PublicProfileLoadingPreview() {
    SpTheme(isDarkTheme = false) {
        PublicProfileContent(PublicProfileUiState(isLoading = true), {})
    }
}
