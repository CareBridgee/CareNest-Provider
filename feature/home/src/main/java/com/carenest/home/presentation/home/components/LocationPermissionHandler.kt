package com.carenest.home.presentation.home.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.carenest.provider.designsystem.components.dialog.CareNestDialog

@Composable
fun LocationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    showRationale: Boolean,
    onRationaleDismissed: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    if (showRationale) {
        CareNestDialog(
            title = "Location Permission Required",
            message = "CareNest needs your location to update your availability so nearby patients can find and request your services.",
            confirmText = "Allow",
            dismissText = "Deny",
            onConfirm = {
                launcher.launch(permissions)
                onRationaleDismissed()
            },
            onDismiss = onRationaleDismissed,
        )
    } else {
        LaunchedEffect(Unit) {
            val hasFineLocation = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val hasCoarseLocation = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasFineLocation || hasCoarseLocation) {
                onPermissionGranted()
            } else {
                val shouldShowRationale = activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                } ?: false

                if (shouldShowRationale) {
                    onPermissionDenied()
                } else {
                    launcher.launch(permissions)
                }
            }
        }
    }
}
