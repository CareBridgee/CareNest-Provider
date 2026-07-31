package com.carenest.request.presentation.ui.offerconfirmed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun CancellationInfoBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Theme.colors.primaryContainer,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.space12),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_info),
            tint = Theme.colors.primary,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = message,
            style = Theme.typography.body.small.copy(
                fontWeight = FontWeight.Normal
            ),
            color = Theme.colors.onPrimaryContainer,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        CancellationInfoBanner(message = "Cancellation info")
    }
}