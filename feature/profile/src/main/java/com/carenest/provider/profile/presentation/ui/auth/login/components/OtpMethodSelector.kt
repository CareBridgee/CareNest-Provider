package com.carenest.presentation.ui.auth.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.carenest.presentation.R
import com.carenest.designsystem.R as DR
import com.carenest.presentation.ui.auth.login.OtpDeliveryMethod

@Composable
fun OtpMethodSelector(
    selectedMethod: OtpDeliveryMethod,
    onMethodSelect: (OtpDeliveryMethod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SquareOptionCard(
            modifier = Modifier.weight(1f),
            title = stringResource(id = R.string.phone_input_via_whatsapp),
            iconRes = DR.drawable.whatsapp, 
            isSelected = selectedMethod == OtpDeliveryMethod.WHATSAPP,
            onClick = { onMethodSelect(OtpDeliveryMethod.WHATSAPP) }
        )

        SquareOptionCard(
            modifier = Modifier.weight(1f),
            title = stringResource(id = R.string.phone_input_via_sms),
            iconRes = DR.drawable.comment_sms,
            isSelected = selectedMethod == OtpDeliveryMethod.SMS,
            onClick = { onMethodSelect(OtpDeliveryMethod.SMS) }
        )
    }
}
