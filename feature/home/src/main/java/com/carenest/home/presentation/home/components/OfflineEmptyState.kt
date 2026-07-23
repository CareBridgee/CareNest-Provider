package com.carenest.home.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.home.R
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun OfflineEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.offline_state),
            contentDescription = "state",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = stringResource(R.string.nurse_requests_offline_title),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
        )

        Text(
            text = stringResource(R.string.nurse_requests_offline_description),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.hint,
            ),
            textAlign = TextAlign.Center
        )
    }
}