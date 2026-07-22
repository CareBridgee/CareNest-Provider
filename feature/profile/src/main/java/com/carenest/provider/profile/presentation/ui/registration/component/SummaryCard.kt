package com.carenest.provider.profile.presentation.ui.registration.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.util.noRippleClickable

@Composable
fun SummaryCard(
    title: String,
    icon: Painter,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Theme.colors.tint,
                modifier = Modifier.size(Theme.size.iconMedium)
            )
            BasicText(
                text = title,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.tint,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_edit),
                contentDescription = "Edit",
                tint = Theme.colors.tint,
                modifier = Modifier
                    .size(Theme.size.iconSmall)
                    .noRippleClickable { onEditClick() }
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        content()
    }
}
