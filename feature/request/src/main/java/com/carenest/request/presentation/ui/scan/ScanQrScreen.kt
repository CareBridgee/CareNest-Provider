package com.carenest.request.presentation.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.toast.SnackbarHost
import com.carenest.provider.designsystem.components.toast.ToastType
import com.carenest.provider.designsystem.components.toast.showSnack
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.presentation.ui.scan.composable.QrCameraScanner
import kotlinx.coroutines.launch
import com.carenest.provider.designsystem.R as RD

@Composable
fun ScanQrScreen(
    requestId: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: ScanQrViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(requestId) {
        viewModel.onIntent(ScanQrIntent.Load(requestId))
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(), onResult = { granted ->
            hasCameraPermission = granted
        })

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            ScanQrEffect.NavigateBack -> onBack()
            ScanQrEffect.NavigateToSuccess -> onSuccess()
            is ScanQrEffect.ShowError -> {
                scope.launch {
                    snackbarHostState.showSnack(
                        message = effect.message,
                        type = ToastType.Error
                    )
                }
            }
        }
    }

    ScanQrContent(
        state = state,
        onIntent = viewModel::onIntent,
        hasPermission = hasCameraPermission,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ScanQrContent(
    state: ScanQrUiState,
    onIntent: (ScanQrIntent) -> Unit,
    hasPermission: Boolean,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            CareNestTopBar(
                title = stringResource(R.string.scan_qr_title),
                leading = TopBarLeading.Back { onIntent(ScanQrIntent.BackClicked) })
        },
        containerColor = Theme.colors.backGround,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BasicText(
                text = stringResource(R.string.scan_qr_instruction),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont, textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(vertical = Theme.spacing.extraLarge)
            )

            Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

            Box(
                modifier = Modifier
                    .size(340.dp)
                    .padding(Theme.spacing.medium),
                contentAlignment = Alignment.Center
            ) {
                if (hasPermission) {
                    QrCameraScanner(
                        onCodeScanned = { code ->
                            if (!state.isLoading && !state.isSuccess) {
                                onIntent(ScanQrIntent.QrScanned(code))
                            }
                        })
                }

                val frameTint = when {
                    state.isSuccess -> Theme.colors.success
                    state.error != null -> Theme.colors.error
                    else -> Theme.colors.primary
                }

                Icon(
                    painter = painterResource(RD.drawable.ic_crop_free),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = frameTint
                )

                if (state.isLoading) {
                    CircularProgressIndicator(color = Theme.colors.primary)
                }

                state.scannedCode?.let {
                    BasicText(
                        text = it,
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.primary,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .padding(Theme.spacing.medium)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

            val statusText = when {
                state.isLoading -> stringResource(R.string.scan_qr_scanning)
                state.error != null -> state.error
                else -> stringResource(R.string.scan_qr_scanning)
            }

            val statusColor = if (state.error != null) Theme.colors.error else Theme.colors.primaryFont

            BasicText(
                text = statusText,
                style = Theme.typography.body.large.copy(
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(vertical = Theme.spacing.extraLarge)
            )

            if (state.error != null) {
                Spacer(modifier = Modifier.height(Theme.spacing.large))
                PrimaryButton(
                    caption = stringResource(R.string.scan_qr_retry),
                    onClick = { onIntent(ScanQrIntent.Retry) },
                    modifier = Modifier.padding(horizontal = Theme.spacing.extraLarge)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        ScanQrContent(ScanQrUiState(), {}, true)
    }
}
