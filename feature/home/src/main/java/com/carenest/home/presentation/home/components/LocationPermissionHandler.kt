package com.carenest.home.presentation.home.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.carenest.home.R
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

    var showSettingsDialog by remember { mutableStateOf(false) }

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
            showSettingsDialog = true
        }
    }

    if (showSettingsDialog) {
        CareNestDialog(
            title = stringResource(R.string.location_permission_required_title),
            message = stringResource(R.string.location_permission_required_message),
            confirmText = stringResource(R.string.location_permission_open_settings),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                showSettingsDialog = false
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
                onPermissionDenied()
            },
            onDismiss = {
                showSettingsDialog = false
                onPermissionDenied()
            },
        )
    } else if (showRationale) {
        CareNestDialog(
            title = stringResource(R.string.location_permission_rationale_title),
            message = stringResource(R.string.location_permission_rationale_message),
            confirmText = stringResource(R.string.location_permission_allow),
            dismissText = stringResource(R.string.location_permission_deny),
            onConfirm = {
                launcher.launch(permissions)
                onRationaleDismissed()
            },
            onDismiss = {
                onRationaleDismissed()
                showSettingsDialog = true
            },
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
