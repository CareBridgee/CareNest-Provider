package com.carenest.home.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.carenest.home.R
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun HomeGreetingBar(
    name: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(2.dp, Theme.colors.primaryVariant, CircleShape)
                .background(Theme.colors.surfaceVariant), contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = avatarUrl?.takeIf(String::isNotBlank),
                contentDescription = "User Avatar",
                placeholder = painterResource(com.carenest.provider.designsystem.R.drawable.nurse_image),
                error = painterResource(com.carenest.provider.designsystem.R.drawable.nurse_image),
                fallback = painterResource(com.carenest.provider.designsystem.R.drawable.nurse_image),
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(Theme.spacing.medium),
        ) {
            Text(
                text = name,
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.Bold, color = Theme.colors.primary
                )
            )

            Text(
                text = stringResource(R.string.welcome_title),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.primary
                )
            )
        }
    }
}
