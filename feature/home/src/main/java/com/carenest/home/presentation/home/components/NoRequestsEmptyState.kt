package com.carenest.home.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun NoRequestsEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.offline_state),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        Text(
            text = stringResource(R.string.nurse_requests_no_requests_title),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            ),
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraSmall))

        Text(
            text = stringResource(R.string.nurse_requests_no_requests_description),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.hint,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Theme.spacing.medium)
        )
    }
}
