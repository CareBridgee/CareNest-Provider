package com.carenest.request.presentation.ui.details.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.carenest.provider.designsystem.R as RD
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.domain.model.Offer

@Composable
fun LocationSection(
    offer: Offer,
    onCopyAddressClicked: () -> Unit,
    onMapClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            text = stringResource(R.string.request_details_location_section),
            style = Theme.typography.body.small.copy(color = Theme.colors.primary, fontWeight = FontWeight.Bold),
        )
        BasicText(
            text = stringResource(R.string.request_details_copy_address),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier.clickable { onCopyAddressClicked() }
        )
    }
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            painter = painterResource(RD.drawable.ic_location),
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(Theme.spacing.small))
        Column {
            BasicText(
                text = offer.patientInfo.addressLine,
                style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont, fontWeight = FontWeight.Bold),
            )
            BasicText(
                text = offer.patientInfo.addressDetail,
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont, fontWeight = FontWeight.Normal),
            )
        }
    }

    Spacer(Modifier.height(Theme.spacing.small))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.colors.surfaceVariant),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Mock Map Background
        AsyncImage(
            model = "https://maps.googleapis.com/maps/api/staticmap?center=London&zoom=13&size=600x300&key=YOUR_API_KEY",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        SecondaryButton(
            caption = stringResource(R.string.request_details_open_maps),
            onClick = onMapClicked,
            modifier = Modifier.padding(Theme.spacing.medium).height(40.dp),
            iconPainter = painterResource(RD.drawable.ic_crop_free)
        )
    }
}