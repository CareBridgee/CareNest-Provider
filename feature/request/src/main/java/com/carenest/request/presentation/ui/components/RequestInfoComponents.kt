package com.carenest.request.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.provider.designsystem.R as RD

@Composable
fun ContactActionsRow(
    onCallClick: () -> Unit,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        ContactIconButton(
            icon = painterResource(com.carenest.provider.designsystem.R.drawable.ic_call),
            contentDescription = stringResource(R.string.request_call_patient),
            onClick = onCallClick,
        )
        ContactIconButton(
            icon = painterResource(com.carenest.provider.designsystem.R.drawable.ic_message),
            contentDescription = stringResource(R.string.request_message_patient),
            onClick = onMessageClick,
        )
    }
}

@Composable
private fun ContactIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.colors.disable.copy(alpha = 0.5f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = Theme.colors.primary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
fun SuccessCheckBadge(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(84.dp)
            .background(color = Theme.colors.primary, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = RD.drawable.ic_check_white),
            contentDescription = null,
            tint = Theme.colors.onPrimary,
            modifier = Modifier.size(40.dp),
        )
    }
}

@Composable
fun RequestDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
            ),
        )
        BasicText(
            text = value,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
fun InfoPill(
    icon: Painter,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(Theme.colors.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = Theme.colors.secondaryFont,
            modifier = Modifier.size(14.dp),
        )
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RequestInfoComponentsPreview() {
    SpTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SuccessCheckBadge()
            ContactActionsRow(onCallClick = {}, onMessageClick = {})
        }
    }
}
