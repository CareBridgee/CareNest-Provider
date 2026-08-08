package com.carenest.provider.profile.presentation.ui.under_review_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.profile.R
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.ActionRequiredScreenContent
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.ActionSection
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.IllustrationSection
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.StatusCardSection
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.SuccessScreenContent
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.TextSection


@Composable
fun UnderReviewScreen(
    nurseId: String,
    viewModel: UnderReviewViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onGoToHomeClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onBackToLoginClick: () -> Unit ,
    onDashboardClick: () -> Unit,
    onCommunityGuidelinesClick: () -> Unit ,
    onUploadAgainClick: (String, String) -> Unit
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(nurseId) { viewModel.load(nurseId) }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            UnderReviewEvent.OnBackClick -> onBackClick()
            UnderReviewEvent.OnGoToHomeClick -> onGoToHomeClick()
            UnderReviewEvent.OnContactSupportClick -> onContactSupportClick()
            UnderReviewEvent.OnBackToLoginClick -> onBackToLoginClick()
        }
    }

    UnderReviewScreenContent(
        state = state,
        onBackClick = {
            viewModel.onIntent(UnderReviewIntent.OnBackClick)
        },
        onGoToHomeClick = {
            viewModel.onIntent(UnderReviewIntent.OnGoToHomeClick)
        },
        onContactSupportClick = {
            viewModel.onIntent(UnderReviewIntent.OnContactSupportClick)
        },
        onBackToLoginClick = {
            viewModel.onIntent(UnderReviewIntent.OnBackToLoginClick)
        },
        onDashboardClick = onDashboardClick,
        onCommunityGuidelinesClick = onCommunityGuidelinesClick,
        onUploadAgainClick = {
            val failed = state.failedSteps.firstOrNull()
            onUploadAgainClick(failed?.step.orEmpty(), failed?.reason ?: state.rejectionReason)
        },
        onRetry = { viewModel.onIntent(UnderReviewIntent.OnRetry) },
    )
}

@Composable
private fun UnderReviewScreenContent(
    state: UnderReviewState,
    onBackClick: () -> Unit,
    onGoToHomeClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onCommunityGuidelinesClick: () -> Unit,
    onUploadAgainClick: () -> Unit,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isSuccess = state.underReviewState == ReviewState.Success
    val isError = state.underReviewState == ReviewState.Error

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CareNestTopBar(
                title = stringResource(R.string.app_name),
                leading = if (isSuccess) null else TopBarLeading.Back(onBackClick),
                trailingAvatarUrl = state.profileImageUrl,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = Theme.colors.backGround
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(Theme.spacing.extraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(color = Theme.colors.primary)
                }
            } else if (state.error != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(Theme.spacing.extraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                ) {
                    BasicText(
                        text = context.resources.getIdentifier(
                            state.error,
                            "string",
                            context.packageName,
                        ).let { id -> if (id != 0) context.getString(id) else state.error },
                        style = Theme.typography.body.medium.copy(color = Theme.colors.error),
                    )
                    PrimaryButton(
                        caption = stringResource(R.string.retry),
                        onClick = onRetry,
                    )
                }
            } else if (isSuccess) {
                SuccessScreenContent(
                    onHomeClick = onDashboardClick,
                    onCommunityGuidelinesClick = onCommunityGuidelinesClick,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (isError) {
                ActionRequiredScreenContent(
                    rejectionReason = state.rejectionReason,
                    onUploadAgainClick = onUploadAgainClick,
                    onContactSupportClick = onContactSupportClick,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Theme.spacing.large, vertical = Theme.spacing.large),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.large)
                ) {
                    Spacer(modifier = Modifier.height(Theme.spacing.medium))

                    IllustrationSection()

                    TextSection()

                    StatusCardSection()

                    Spacer(modifier = Modifier.height(Theme.spacing.small))

                    ActionSection(
                        onGoToHomeClick = onGoToHomeClick,
                        onContactSupportClick = onContactSupportClick,
                        onBackToLoginClick = onBackToLoginClick
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun UnderReviewScreenContentLightPreview() {
    SpTheme(isDarkTheme = false) {
        UnderReviewScreenContent(
            state = UnderReviewState(underReviewState = ReviewState.UnderReview),
            onBackClick = {},
            onGoToHomeClick = {},
            onContactSupportClick = {},
            onBackToLoginClick = {},
            onDashboardClick = {},
            onCommunityGuidelinesClick = {},
            onUploadAgainClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnderReviewScreenSuccessLightPreview() {
    SpTheme(isDarkTheme = false) {
        UnderReviewScreenContent(
            state = UnderReviewState(underReviewState = ReviewState.Success),
            onBackClick = {},
            onGoToHomeClick = {},
            onContactSupportClick = {},
            onBackToLoginClick = {},
            onDashboardClick = {},
            onCommunityGuidelinesClick = {},
            onUploadAgainClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnderReviewScreenErrorLightPreview() {
    SpTheme(isDarkTheme = false) {
        UnderReviewScreenContent(
            state = UnderReviewState(underReviewState = ReviewState.Error),
            onBackClick = {},
            onGoToHomeClick = {},
            onContactSupportClick = {},
            onBackToLoginClick = {},
            onDashboardClick = {},
            onCommunityGuidelinesClick = {},
            onUploadAgainClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnderReviewScreenContentDarkPreview() {
    SpTheme(isDarkTheme = true) {
        UnderReviewScreenContent(
            state = UnderReviewState(underReviewState = ReviewState.UnderReview),
            onBackClick = {},
            onGoToHomeClick = {},
            onContactSupportClick = {},
            onBackToLoginClick = {},
            onDashboardClick = {},
            onCommunityGuidelinesClick = {},
            onUploadAgainClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnderReviewScreenSuccessDarkPreview() {
    SpTheme(isDarkTheme = true) {
        UnderReviewScreenContent(
            state = UnderReviewState(underReviewState = ReviewState.Success),
            onBackClick = {},
            onGoToHomeClick = {},
            onContactSupportClick = {},
            onBackToLoginClick = {},
            onDashboardClick = {},
            onCommunityGuidelinesClick = {},
            onUploadAgainClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnderReviewScreenErrorDarkPreview() {
    SpTheme(isDarkTheme = true) {
        UnderReviewScreenContent(
            state = UnderReviewState(underReviewState = ReviewState.Error),
            onBackClick = {},
            onGoToHomeClick = {},
            onContactSupportClick = {},
            onBackToLoginClick = {},
            onDashboardClick = {},
            onCommunityGuidelinesClick = {},
            onUploadAgainClick = {}
        )
    }
}
