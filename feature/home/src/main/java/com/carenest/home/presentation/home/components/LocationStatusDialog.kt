package com.carenest.home.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.carenest.home.R
import com.carenest.provider.core.location.LocationData
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun LocationStatusDialog(
    isGettingLocation: Boolean,
    determinedLocation: LocationData?,
    locationError: String?,
    onConfirmOnline: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = when {
        isGettingLocation -> stringResource(R.string.getting_location_title)
        determinedLocation != null -> stringResource(R.string.location_determined_title)
        else -> stringResource(R.string.location_unknown_title)
    }

    val message = when {
        isGettingLocation -> stringResource(R.string.getting_location_message)
        determinedLocation != null -> stringResource(
            R.string.location_determined_message,
            determinedLocation.latitude,
            determinedLocation.longitude
        )
        else -> locationError ?: stringResource(R.string.unable_to_get_location)
    }

    val iconBgColor = if (locationError != null && !isGettingLocation && determinedLocation == null) {
        Theme.colors.error.copy(alpha = 0.12f)
    } else {
        Theme.colors.primary.copy(alpha = 0.12f)
    }

    val confirmButtonColor = if (locationError != null && !isGettingLocation && determinedLocation == null) {
        Theme.colors.error
    } else {
        Theme.colors.primary
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Theme.colors.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                if (isGettingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = Theme.colors.primary,
                        strokeWidth = 3.dp,
                    )
                } else if (determinedLocation != null) {
                    Image(
                        painter = painterResource(com.carenest.provider.designsystem.R.drawable.ic_location),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        colorFilter = ColorFilter.tint(Theme.colors.primary),
                    )
                } else {
                    Image(
                        painter = painterResource(com.carenest.provider.designsystem.R.drawable.common_error_icon),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        colorFilter = ColorFilter.tint(Theme.colors.error),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = title,
                style = Theme.typography.title,
                fontWeight = FontWeight.Bold,
                color = Theme.colors.primaryFont,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = message,
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (isGettingLocation) {
                    PrimaryButton(
                        caption = stringResource(R.string.cancel),
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Theme.colors.surfaceVariant,
                        contentColor = Theme.colors.primary,
                    )
                } else if (determinedLocation != null) {
                    PrimaryButton(
                        caption = stringResource(R.string.cancel),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        containerColor = Theme.colors.surfaceVariant,
                        contentColor = Theme.colors.primary,
                    )
                    PrimaryButton(
                        caption = stringResource(R.string.go_online),
                        onClick = onConfirmOnline,
                        modifier = Modifier.weight(1f),
                        containerColor = confirmButtonColor,
                        contentColor = Theme.colors.onPrimary,
                    )
                } else {
                    PrimaryButton(
                        caption = stringResource(R.string.cancellation_dialog_ok),
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = confirmButtonColor,
                        contentColor = Theme.colors.onPrimary,
                    )
                }
            }
        }
    }
}
