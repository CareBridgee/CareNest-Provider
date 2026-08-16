package com.carenest.provider.account.presentation.publicprofile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.components.ProviderAccountTopBar
import com.carenest.provider.account.presentation.components.ProviderStatisticCard
import com.carenest.provider.account.presentation.components.PublicProfileLoadingSkeleton
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.bottomsheet.BaseBottomSheet
import com.carenest.provider.designsystem.components.avatar.ProfileAvatar
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.components.textfield.CustomTextField
import com.carenest.provider.designsystem.components.toast.ToastHost
import com.carenest.provider.designsystem.components.toast.rememberToastState
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.domain.model.NurseProfile
import com.carenest.provider.profile.domain.model.NurseService
import com.carenest.provider.profile.domain.model.VerificationStatus
import com.carenest.provider.designsystem.R as DesignSystemR

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
    val context = LocalContext.current
    val toastState = rememberToastState()
    val profileImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let {
            persistReadPermission(context, it)
            viewModel.onIntent(
                PublicProfileIntent.ProfileImagePicked(
                    PickedProfileImage(
                        uri = it,
                        fileName = getFileName(context, it),
                        mimeType = context.contentResolver.getType(it) ?: "application/octet-stream",
                    ),
                ),
            )
        }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            PublicProfileEffect.NavigateBack -> onNavigateBack()
            PublicProfileEffect.OpenProfileImagePicker -> profileImagePicker.launch(arrayOf("image/*"))
            PublicProfileEffect.ShareProfile -> onShareProfile()
            PublicProfileEffect.OpenSettings -> onOpenSettings()
            is PublicProfileEffect.ShowMessage -> {
                toastState.show(
                    message = resolveMessage(context, effect.message),
                    type = effect.type,
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        PublicProfileContent(state, viewModel::onIntent, Modifier.fillMaxSize())
        ToastHost(state = toastState)
    }
}

@Composable
fun PublicProfileContent(
    state: PublicProfileUiState,
    onIntent: (PublicProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isEditBioSheetVisible) {
        EditBioBottomSheet(
            state = state,
            onIntent = onIntent,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        ProviderAccountTopBar(
            showSettings = true,
            avatarUrl = state.profileImageUrl,
            onSettingsClick = { onIntent(PublicProfileIntent.SettingsClicked) },
            onNavigateBack = { onIntent(PublicProfileIntent.BackClicked) },
        )
        when {
            state.isLoading -> PublicProfileLoadingSkeleton()
            state.profile == null && state.errorMessage != null -> {
                ProfileErrorContent(
                    message = state.errorMessage,
                    onRetry = { onIntent(PublicProfileIntent.RetryClicked) },
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Theme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                ) {
                    item {
                        PublicProfileHero(
                            state = state,
                            onProfileImageClick = { onIntent(PublicProfileIntent.ProfileImageClicked) },
                        )
                    }
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            ProfileActionButton(
                                label = stringResource(R.string.public_profile_edit_bio),
                                iconRes = DesignSystemR.drawable.ic_account_edit_address,
                                primary = true,
                                onClick = { onIntent(PublicProfileIntent.EditBioClicked) },
                                modifier = Modifier.weight(1f),
                            )
                            ProfileActionButton(
                                label = stringResource(R.string.public_profile_share),
                                iconRes = DesignSystemR.drawable.ic_account_share,
                                primary = false,
                                onClick = { onIntent(PublicProfileIntent.ShareProfileClicked) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    item { AboutMeCard(state) }
                }
            }
        }
    }
}

@Composable
private fun PublicProfileHero(
    state: PublicProfileUiState,
    onProfileImageClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.primaryContainer)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(116.dp)
                .clickable(onClick = onProfileImageClick),
            contentAlignment = Alignment.Center,
        ) {
            ProfileAvatar(
                imageUrl = state.profileImageUrl,
                contentDescription = stringResource(R.string.account_profile_photo),
                modifier = Modifier
                    .fillMaxSize()
                    .border(5.dp, Theme.colors.surface, CircleShape),
            )
            if (state.isUploadingProfileImage) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Theme.colors.surface.copy(alpha = 0.72f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = Theme.colors.primary,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_edit),
                    contentDescription = stringResource(R.string.public_profile_change_photo),
                    tint = Theme.colors.onPrimary,
                    modifier = Modifier.size(17.dp),
                )
            }
            if (state.isVerified) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-4).dp, y = 4.dp)
                        .size(34.dp)
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
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(Theme.spacing.medium))
        BasicText(
            text = state.fullName.ifBlank { stringResource(R.string.public_profile_unknown_name) },
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
                text = state.specialization.ifBlank {
                    stringResource(R.string.public_profile_no_specialization)
                },
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
                state.ratingText,
                pluralStringResource(R.plurals.reviews_count, state.reviewCount, state.reviewCount),
                Modifier.weight(1f),
            )
            ProviderStatisticCard(
                pluralStringResource(
                    R.plurals.public_profile_experience_years,
                    state.yearsOfExperience,
                    state.yearsOfExperience,
                ),
                stringResource(R.string.public_profile_experience),
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProfileActionButton(
    label: String,
    @DrawableRes iconRes: Int,
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
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (primary) Theme.colors.onPrimary else Theme.colors.tint,
        )
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
private fun AboutMeCard(state: PublicProfileUiState) {
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
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_account_about_me),
                contentDescription = null,
                tint = Theme.colors.tint,
            )
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
            text = state.bio.ifBlank { stringResource(R.string.public_profile_no_bio) },
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp,
            ),
        )
        if (state.serviceNames.isEmpty()) {
            BasicText(
                text = stringResource(R.string.public_profile_no_services),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Normal,
                ),
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                state.serviceNames.forEach { service ->
                    BasicText(
                        text = service,
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.secondaryFont,
                            fontWeight = FontWeight.Normal,
                        ),
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Theme.colors.primaryContainer)
                            .padding(
                                horizontal = Theme.spacing.medium,
                                vertical = Theme.spacing.extraSmall,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EditBioBottomSheet(
    state: PublicProfileUiState,
    onIntent: (PublicProfileIntent) -> Unit,
) {
    BaseBottomSheet(
        onDismissRequest = { onIntent(PublicProfileIntent.DismissEditBio) },
        title = stringResource(R.string.public_profile_edit_bio),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        ) {
            CustomTextField(
                text = state.specializationDraft,
                onTextChange = { onIntent(PublicProfileIntent.SpecializationChanged(it)) },
                title = stringResource(R.string.public_profile_specialization_label),
                hint = stringResource(R.string.public_profile_specialization_hint),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            CustomTextField(
                text = state.yearsOfExperienceDraft,
                onTextChange = { onIntent(PublicProfileIntent.YearsOfExperienceChanged(it)) },
                title = stringResource(R.string.public_profile_years_label),
                hint = stringResource(R.string.public_profile_years_hint),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            CustomTextField(
                text = state.bioDraft,
                onTextChange = { onIntent(PublicProfileIntent.BioChanged(it)) },
                title = stringResource(R.string.public_profile_bio_label),
                hint = stringResource(R.string.public_profile_bio_hint),
                minLines = 4,
                maxLines = 6,
                fieldHeight = 140.dp,
                fieldVerticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
            ) {
                SecondaryButton(
                    caption = stringResource(R.string.public_profile_cancel),
                    onClick = { onIntent(PublicProfileIntent.DismissEditBio) },
                    modifier = Modifier.weight(1f),
                    isDisabled = state.isSavingProfile,
                )
                PrimaryButton(
                    caption = stringResource(R.string.public_profile_save),
                    onClick = { onIntent(PublicProfileIntent.SaveBioClicked) },
                    modifier = Modifier.weight(1f),
                    isLoading = state.isSavingProfile,
                    isDisabled = state.isSavingProfile,
                )
            }
        }
    }
}

@Composable
private fun ProfileErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BasicText(
            text = resolveMessage(LocalContext.current, message),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center,
            ),
        )
        Spacer(Modifier.height(Theme.spacing.medium))
        PrimaryButton(
            caption = stringResource(R.string.public_profile_retry),
            onClick = onRetry,
        )
    }
}

@Preview(showBackground = true, heightDp = 860)
@Composable
private fun PublicProfileLightPreview() {
    SpTheme(isDarkTheme = false) {
        PublicProfileContent(
            PublicProfileUiState(profile = previewProfile()),
            {},
        )
    }
}

@Preview(showBackground = true, heightDp = 860)
@Composable
private fun PublicProfileDarkPreview() {
    SpTheme(isDarkTheme = true) {
        PublicProfileContent(
            PublicProfileUiState(profile = previewProfile()),
            {},
        )
    }
}

@Preview(showBackground = true, heightDp = 860)
@Composable
private fun PublicProfileLoadingPreview() {
    SpTheme(isDarkTheme = false) {
        PublicProfileContent(PublicProfileUiState(isLoading = true), {})
    }
}

private fun previewProfile() = NurseProfile(
    id = "nurse-preview",
    firstName = "Sarah",
    lastName = "Mitchell",
    profileImageUrl = null,
    specialization = "Home Health Nursing",
    yearsOfExperience = 8,
    bio = "Dedicated registered nurse focused on compassionate home care.",
    ratingAvg = 4.9,
    totalReviews = 124,
    verificationStatus = VerificationStatus.APPROVED,
    rejectionReason = null,
    failedSteps = emptyList(),
    services = listOf(
        NurseService(
            id = "service-1",
            serviceTypeId = "post-op",
            serviceName = "Post-Op Recovery",
            serviceDescription = null,
            basePrice = 80.0,
            isActive = true,
        ),
    ),
)
