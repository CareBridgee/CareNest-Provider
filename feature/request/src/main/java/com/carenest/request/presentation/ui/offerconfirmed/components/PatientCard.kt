package com.carenest.request.presentation.ui.offerconfirmed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun PatientCard(
    name: String,
    estimatedArrivalTime: String,
    onCallClick: () -> Unit,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Theme.colors.surface,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PatientAvatar()
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = Theme.typography.title,
                    color = Theme.colors.primaryFont,
                    maxLines = 1,
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 14.dp),
            color = Theme.colors.divider,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.estimated_time),
                    style = Theme.typography.body.small.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    color = Theme.colors.secondaryFont,
                )
                Text(
                    text = estimatedArrivalTime,
                    style = Theme.typography.title,
                    color = Theme.colors.primary,
                )
            }
            Row {
                ActionIconButton(
                    icon = painterResource(RD.drawable.ic_phone),
                    contentDescription = "ic phone",
                    onClick = onCallClick,
                )
                Spacer(modifier = Modifier.width(10.dp))
                ActionIconButton(
                    icon = painterResource(RD.drawable.ic_message),
                    contentDescription = "ic message",
                    onClick = onMessageClick,
                )
            }
        }
    }
}

@Composable
private fun PatientAvatar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .size(56.dp)
            .background(
                color = Theme.colors.primaryContainer,
                shape = RoundedCornerShape(14.dp),
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = Theme.colors.onPrimaryContainer,
        )
    }
}

@Composable
private fun ActionIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .background(
                color = Theme.colors.disable,
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = Theme.colors.primary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        PatientCard(
            name = "Mark Harrison",
            estimatedArrivalTime = "9:05 am",
            onCallClick = {},
            onMessageClick = {},
        )
    }
}