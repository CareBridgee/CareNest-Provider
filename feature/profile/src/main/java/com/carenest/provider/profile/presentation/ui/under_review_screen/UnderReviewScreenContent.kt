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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.ActionRequiredScreenContent
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.ActionSection
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.IllustrationSection
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.StatusCardSection
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.SuccessScreenContent
import com.carenest.provider.profile.presentation.ui.under_review_screen.composable.TextSection


@Composable
fun UnderReviewScreen(
    viewModel: UnderReviewViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onGoToHomeClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onBackToLoginClick: () -> Unit ,
    onDashboardClick: () -> Unit,
    onCommunityGuidelinesClick: () -> Unit ,
    onUploadAgainClick: () -> Unit
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

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
        onUploadAgainClick = onUploadAgainClick
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
    modifier: Modifier = Modifier,
    avatarUrl: String? = "https://lh3.googleusercontent.com/aida-public/AB6AXuDoegGFZVcEdHy8-NusuyjiS-d6Mty4Z4EoczLydOs8RCH1zvj5FBvxfwB_Wl2j6kUh7deCM2rssQWpgYWQY6Oav8w0byJe0JalttPlE9e1EXlfaSDxKJKO1R6bKp12FmxlQpg6vVIu_pfxOZ-0ciCgcWtCnUzel2KkM7ZifGFYuxwLYAyu4xZnibHr2zhLco364uLun4eawcpEtsxS9WOY6FoAnls0O2B-56k4HrMkouY0q9fDVSNg"
) {
    val isSuccess = state.underReviewState == ReviewState.Success
    val isError = state.underReviewState == ReviewState.Error

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CareNestTopBar(
                title = stringResource(R.string.app_name),
                leading = if (isSuccess) null else TopBarLeading.Back(onBackClick),
                trailingAvatarUrl = avatarUrl,
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
            if (isSuccess) {
                SuccessScreenContent(
                    onHomeClick = onDashboardClick,
                    onCommunityGuidelinesClick = onCommunityGuidelinesClick,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (isError) {
                ActionRequiredScreenContent(
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
