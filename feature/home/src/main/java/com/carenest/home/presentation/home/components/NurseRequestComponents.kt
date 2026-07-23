package com.carenest.home.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.home.R
import com.carenest.home.domain.model.RequestStatus
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun RequestStatusBadge(
    status: RequestStatus,
    modifier: Modifier = Modifier,
) {
    val label = when (status) {
        RequestStatus.ESTIMATED -> stringResource(R.string.nurse_requests_status_estimated)
        RequestStatus.CANCELED -> stringResource(R.string.nurse_requests_status_canceled)
        RequestStatus.ACCEPTED -> stringResource(R.string.nurse_requests_status_accepted)
    }

    val containerColor by animateColorAsState(
        targetValue = when (status) {
            RequestStatus.ESTIMATED -> Theme.colors.primaryContainer
            RequestStatus.CANCELED -> Theme.colors.errorContainer
            RequestStatus.ACCEPTED -> Theme.colors.successContainer
        },
        animationSpec = tween(300),
        label = "statusBadgeBg",
    )
    val contentColor by animateColorAsState(
        targetValue = when (status) {
            RequestStatus.ESTIMATED -> Theme.colors.onPrimaryContainer
            RequestStatus.CANCELED -> Theme.colors.onErrorContainer
            RequestStatus.ACCEPTED -> Theme.colors.onSuccessContainer
        },
        animationSpec = tween(300),
        label = "statusBadgeFg",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            ),
        )
    }
}

@Composable
fun NurseRequestDetailRow(
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
                fontSize = 13.sp,
            ),
        )
        BasicText(
            text = value,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            ),
        )
    }
}

@Composable
fun OnlineToggleCard(
    isOnline: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusLabel = if (isOnline) stringResource(R.string.online) else stringResource(R.string.offline)
    val statusColor = if(isOnline) Theme.colors.primary else Theme.colors.hint

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        border = BorderStroke(
            width = 1.dp,
            color = Theme.colors.disable.copy(alpha = 0.10f),
        ),
        colors = CardDefaults.cardColors(containerColor = Theme.colors.onPrimary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.medium, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(statusColor)
            )

            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                BasicText(
                    text = stringResource(R.string.nurse_requests_online_title),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                )

                Spacer(Modifier.width(5.dp))

                BasicText(
                    text = statusLabel,
                    style = Theme.typography.body.small.copy(
                        color =  statusColor
                    ),
                )
            }

            Switch(
                checked = isOnline,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                    checkedTrackColor = Theme.colors.tint,
                    uncheckedThumbColor = androidx.compose.ui.graphics.Color.White,
                    uncheckedTrackColor = Theme.colors.track,
                    uncheckedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                ),
                modifier = Modifier.size(width = 48.dp, height = 32.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        RequestStatusBadge(status = RequestStatus.ESTIMATED)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSwitch(){
    SpTheme {
       OnlineToggleCard(isOnline = true,{})
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewRequest(){
    SpTheme {
       NurseRequestDetailRow(label = "Patient Name", value = "John Doe")
    }
}