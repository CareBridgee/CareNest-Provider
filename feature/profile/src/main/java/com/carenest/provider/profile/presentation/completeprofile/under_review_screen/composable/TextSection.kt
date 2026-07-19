package com.carenest.provider.profile.presentation.completeprofile.under_review_screen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle

import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R

@Composable
fun TextSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        Text(
            text = stringResource(R.string.application_under_review),
            style = Theme.typography.displayMedium,
            color = Theme.colors.primaryFont,
            textAlign = TextAlign.Center
        )

        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.user_message))
                withStyle(style = SpanStyle(color = Theme.colors.primary, fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.verification_time))
                }
                append(".")
            },
            style = Theme.typography.body.medium,
            color = Theme.colors.secondaryFont,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Theme.spacing.medium)
        )
    }
}