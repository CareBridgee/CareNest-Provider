package com.carenest.provider.designsystem.components.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun StatusChip(
    label: String,
    icon: Painter,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = contentColor,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
fun VerifiedStatusChip(modifier: Modifier = Modifier) {
    StatusChip(
        label = stringResource(id = R.string.status_verified),
        icon = painterResource(id = R.drawable.ic_check),
        containerColor = Theme.colors.successContainer,
        contentColor = Theme.colors.onSuccessContainer,
        modifier = modifier
    )
}

@Composable
fun PendingStatusChip(modifier: Modifier = Modifier) {
    StatusChip(
        label = stringResource(id = R.string.status_pending),
        icon = painterResource(id = R.drawable.ic_time),
        containerColor = Theme.colors.warningContainer,
        contentColor = Theme.colors.onWarningContainer,
        modifier = modifier
    )
}
