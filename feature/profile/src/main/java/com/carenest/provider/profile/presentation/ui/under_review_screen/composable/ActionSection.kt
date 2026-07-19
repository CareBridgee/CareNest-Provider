package com.carenest.provider.profile.presentation.ui.under_review_screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R
import com.carenest.provider.designsystem.R as RD

@Composable
fun ActionSection(
    onGoToHomeClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            caption = stringResource(R.string.go_to_home_view_only),
            onClick = onGoToHomeClick,
            isDisabled = true,
            modifier = Modifier.fillMaxWidth(),
        )

        SecondaryButton(
            caption = stringResource(R.string.contact_support),
            onClick = onContactSupportClick,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.back_to_login),
            style = Theme.typography.body.small.copy(fontWeight = FontWeight.Bold),
            color = Theme.colors.primary,
            modifier = Modifier
                .clip(Theme.shapes.small)
                .clickable(onClick = onBackToLoginClick)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Theme.shapes.extraLarge)
                .background(Theme.colors.surface)
                .border(1.dp, Theme.colors.divider, Theme.shapes.extraLarge)
                .padding(Theme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_notification),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Theme.colors.primary
                )
            }

            Column {
                Text(
                    text = stringResource(R.string.notify_email),
                    style = Theme.typography.body.small,
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.account_activated),
                    style = Theme.typography.hint.large,
                    color = Theme.colors.secondaryFont
                )
            }
        }
    }
}