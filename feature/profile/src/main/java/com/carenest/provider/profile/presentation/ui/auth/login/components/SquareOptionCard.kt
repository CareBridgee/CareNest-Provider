package com.carenest.presentation.ui.auth.login.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme

@Composable
fun SquareOptionCard(
    modifier: Modifier = Modifier,
    title: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Theme.colors.primary.copy(alpha = 0.05f) else Theme.colors.cardBackground
    val borderColor = if (isSelected) Theme.colors.primary else Color.Transparent

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(2.dp, borderColor, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = Theme.colors.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.colors.primary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
