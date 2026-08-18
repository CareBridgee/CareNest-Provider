package com.carenest.home.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.carenest.home.R
import com.carenest.provider.core.location.LocationData
import com.carenest.provider.designsystem.components.dialog.CareNestDialog
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

    if (isGettingLocation) {
        CareNestDialog(
            title = title,
            message = message,
            confirmText = stringResource(R.string.cancel),
            onConfirm = onDismiss,
            onDismiss = onDismiss,
            dismissText = null,
            customHeader = {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = Theme.colors.primary,
                        strokeWidth = 3.dp,
                    )
                }
            }
        )
    } else if (determinedLocation != null) {
        CareNestDialog(
            title = title,
            message = message,
            confirmText = stringResource(R.string.go_online),
            onConfirm = onConfirmOnline,
            dismissText = stringResource(R.string.cancel),
            onDismiss = onDismiss,
            icon = painterResource(com.carenest.provider.designsystem.R.drawable.ic_location),
            confirmColor = Theme.colors.primary,
        )
    } else {
        CareNestDialog(
            title = title,
            message = message,
            confirmText = stringResource(R.string.cancellation_dialog_ok),
            onConfirm = onDismiss,
            dismissText = null,
            onDismiss = onDismiss,
            icon = painterResource(com.carenest.provider.designsystem.R.drawable.common_error_icon),
            confirmColor = Theme.colors.error,
        )
    }
}
