package com.carenest.provider.profile.presentation.completeprofile.under_review_screen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R
import com.carenest.provider.profile.presentation.completeprofile.under_review_screen.composable.ActionSection
import com.carenest.provider.profile.presentation.completeprofile.under_review_screen.composable.IllustrationSection
import com.carenest.provider.profile.presentation.completeprofile.under_review_screen.composable.StatusCardSection
import com.carenest.provider.profile.presentation.completeprofile.under_review_screen.composable.TextSection


@Composable
fun UnderReviewScreen(
    modifier: Modifier = Modifier
){

    UnderReviewScreenContent(
        modifier = modifier,
        onBackClick = {},
        onGoToHomeClick = {},
        onContactSupportClick = {},
        onBackToLoginClick = {})
}
@Composable
private fun UnderReviewScreenContent(
    onBackClick: () -> Unit,
    onGoToHomeClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    avatarUrl: String? = "https://lh3.googleusercontent.com/aida-public/AB6AXuDoegGFZVcEdHy8-NusuyjiS-d6Mty4Z4EoczLydOs8RCH1zvj5FBvxfwB_Wl2j6kUh7deCM2rssQWpgYWQY6Oav8w0byJe0JalttPlE9e1EXlfaSDxKJKO1R6bKp12FmxlQpg6vVIu_pfxOZ-0ciCgcWtCnUzel2KkM7ZifGFYuxwLYAyu4xZnibHr2zhLco364uLun4eawcpEtsxS9WOY6FoAnls0O2B-56k4HrMkouY0q9fDVSNg"
) {
    Scaffold(
        modifier = modifier.fillMaxSize(), topBar = {
            CareNestTopBar(
                title = stringResource(R.string.app_name),
                leading = TopBarLeading.Back(onBackClick),
                trailingAvatarUrl = avatarUrl,
                modifier = Modifier.fillMaxWidth()
            )
        }, containerColor = Theme.colors.backGround
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            IllustrationSection()

            TextSection()

            StatusCardSection()

            Spacer(modifier = Modifier.height(8.dp))

            ActionSection(
                onGoToHomeClick = onGoToHomeClick,
                onContactSupportClick = onContactSupportClick,
                onBackToLoginClick = onBackToLoginClick
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun UnderReviewScreenContentLightPreview() {
    SpTheme(isDarkTheme = false) {
        UnderReviewScreenContent(
            onBackClick = {},
            onGoToHomeClick = {},
            onContactSupportClick = {},
            onBackToLoginClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun UnderReviewScreenContentDarkPreview() {
    SpTheme(isDarkTheme = true) {
        UnderReviewScreenContent(
            onBackClick = {},
            onGoToHomeClick = {},
            onContactSupportClick = {},
            onBackToLoginClick = {})
    }
}
