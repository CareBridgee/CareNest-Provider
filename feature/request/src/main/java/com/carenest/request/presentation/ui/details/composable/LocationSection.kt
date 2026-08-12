package com.carenest.request.presentation.ui.details.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.carenest.provider.designsystem.R as RD
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.util.noRippleClickable
import com.carenest.request.BuildConfig
import com.carenest.request.R
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.PatientLocationDetails
import java.util.Locale

private const val MAP_ZOOM = 13

private fun buildMapboxSnapshotUrl(
    latitude: Double,
    longitude: Double,
): String {
    val lon = String.format(Locale.US, "%.6f", longitude)
    val lat = String.format(Locale.US, "%.6f", latitude)
    val marker = "pin-s+e53935($lon,$lat)"
    return "https://api.mapbox.com/styles/v1/mapbox/streets-v12/static/" +
        "$marker/$lon,$lat,$MAP_ZOOM,0/700x300@2x" +
        "?access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}"
}

@Composable
fun LocationSection(
    offer: Offer,
    location: PatientLocationDetails?,
    isAddressLoading: Boolean,
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
            modifier = Modifier.noRippleClickable(onClick = onCopyAddressClicked)
        )
    }

    val endpointAddress = listOf(
        offer.patientInfo.addressLine,
        offer.patientInfo.addressDetail,
    ).filter(String::isNotBlank).joinToString(", ")
    val address = location?.address?.takeIf(String::isNotBlank) ?: endpointAddress
    val addressDetail = if (location != null) {
        listOf(location.apartment, location.district)
            .filter(String::isNotBlank)
            .distinct()
            .joinToString(", ")
    } else {
        offer.patientInfo.addressDetail
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        Icon(
            painter = painterResource(RD.drawable.ic_location),
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(22.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = when {
                    address.isNotBlank() -> address
                    isAddressLoading -> stringResource(R.string.request_details_finding_address)
                    else -> stringResource(R.string.patient_address_unavailable)
                },
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (addressDetail.isNotBlank() && addressDetail != address) {
                BasicText(
                    text = addressDetail,
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Normal,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }

    val latitude = offer.patientInfo.latitude
    val longitude = offer.patientInfo.longitude
    val mapSnapshotUrl = remember(latitude, longitude) {
        if (latitude != null && longitude != null && BuildConfig.MAPBOX_ACCESS_TOKEN.isNotBlank()) {
            buildMapboxSnapshotUrl(latitude, longitude)
        } else {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(142.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.colors.surfaceVariant)
            .noRippleClickable(onClick = onMapClicked),
        contentAlignment = Alignment.Center,
    ) {
        if (mapSnapshotUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(mapSnapshotUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

    }

    SecondaryButton(
        caption = stringResource(R.string.request_details_open_maps),
        onClick = onMapClicked,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        iconPainter = painterResource(RD.drawable.ic_location),
    )
}
