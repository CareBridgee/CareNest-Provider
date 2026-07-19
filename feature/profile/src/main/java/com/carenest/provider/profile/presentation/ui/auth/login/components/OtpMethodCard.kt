package com.carenest.presentation.ui.auth.login.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme

@Composable
fun OtpMethodCard(
    title: String,
    subtitle: String,
    icon: Painter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) Theme.colors.primary else Theme.colors.disable,
        animationSpec = tween(durationMillis = 300), label = "BorderColor"
    )

    val elevation by animateDpAsState(
        targetValue = if (selected) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 300), label = "Elevation"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) Theme.colors.surface else Theme.colors.backGround,
        animationSpec = tween(durationMillis = 300), label = "BackgroundColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null, // onClick is handled by the Card
                colors = RadioButtonDefaults.colors(
                    selectedColor = Theme.colors.primary,
                    unselectedColor = Theme.colors.hint
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                BasicText(
                    text = title,
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold
                    )
                )
                BasicText(
                    text = subtitle,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont
                    )
                )
            }
        }
    }
}
