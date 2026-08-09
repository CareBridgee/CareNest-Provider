package com.carenest.request.presentation.ui.offerconfirmed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.provider.designsystem.R as RD


@Composable
fun OfferConfirmedActionButtons(
    onShowQrCodeClick: () -> Unit,
    onCancelClick: () -> Unit,
    onShowOfferDetailsClick: () -> Unit,
    isCancelling: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SecondaryButton(
            caption = stringResource(R.string.show_offer_details),
            onClick = onShowOfferDetailsClick,
            modifier = Modifier.fillMaxWidth(),
            iconPainter = painterResource(RD.drawable.ic_file),
        )

        PrimaryButton(
            caption = stringResource(R.string.show_scan_qr),
            onClick = onShowQrCodeClick,
            modifier = Modifier.fillMaxWidth(),
        )

        PrimaryButton(
            caption = stringResource(R.string.cancel),
            onClick = onCancelClick,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Theme.colors.errorContainer,
            contentColor = Theme.colors.onErrorContainer,
            isDisabled = isCancelling,
            isLoading = isCancelling,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        OfferConfirmedActionButtons(
            onShowQrCodeClick = {},
            onCancelClick = {},
            isCancelling = false,
            onShowOfferDetailsClick = {}
        )
    }
}
