package com.carenest.chat.presentation.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun DateSeparatorPill(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = Theme.typography.hint.small.copy(
                fontWeight = FontWeight.Normal
            ),
            color = Theme.colors.secondaryFont,
            modifier = Modifier.background(Theme.colors.hint.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
                .padding(Theme.spacing.space12, vertical = Theme.spacing.extraSmall),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        DateSeparatorPill(label = "Today, 10:42 AM")
    }
}