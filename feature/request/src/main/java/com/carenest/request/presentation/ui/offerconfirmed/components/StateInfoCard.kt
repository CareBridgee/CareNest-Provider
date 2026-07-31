package com.carenest.request.presentation.ui.offerconfirmed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun StateInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Theme.colors.surface,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = Theme.colors.primaryContainer,
                    shape = CircleShape,
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Theme.colors.onPrimaryContainer,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(modifier = Modifier.padding(top = 8.dp))
        Text(
            text = label,
            style = Theme.typography.hint.medium,
            color = Theme.colors.secondaryFont,
        )
        Text(
            text = value,
            style = Theme.typography.title,
            color = Theme.colors.primaryFont,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        StateInfoCard(
            icon = Icons.Filled.Person,
            label = "Reviews",
            value = "5",
        )
    }
}