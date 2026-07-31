package com.carenest.request.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.carenest.request.domain.model.CancellationReason
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.components.textfield.CustomTextField
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R

@Composable
fun cancellationReasonLabel(reason: CancellationReason): String = when (reason) {
    CancellationReason.VEHICLE_ISSUE -> stringResource(R.string.cancel_reason_vehicle_issue)
    CancellationReason.PERSONAL_EMERGENCY -> stringResource(R.string.cancel_reason_personal_emergency)
    CancellationReason.LOCATION_INACCESSIBLE -> stringResource(R.string.cancel_reason_location_inaccessible)
    CancellationReason.SAFETY_CONCERN -> stringResource(R.string.cancel_reason_safety_concern)
    CancellationReason.INCORRECT_PATIENT_DETAILS -> stringResource(R.string.cancel_reason_incorrect_details)
    CancellationReason.INAPPROPRIATE_CONDUCT -> stringResource(R.string.cancel_reason_inappropriate_conduct)
    CancellationReason.OTHER -> stringResource(R.string.cancel_reason_other)
}

@Composable
fun CancelRequestDialog(
    selectedReason: CancellationReason?,
    onReasonSelected: (CancellationReason) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Theme.colors.surface)
                .padding(24.dp),
        ) {
            BasicText(
                text = stringResource(R.string.cancel_request_title),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(Modifier.height(8.dp))

            BasicText(
                text = stringResource(R.string.cancel_request_message),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                ),
            )

            Spacer(Modifier.height(20.dp))

            BasicText(
                text = stringResource(R.string.cancel_request_reason_label),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.SemiBold,
                ),
            )

            Spacer(Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                CancellationReason.entries.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(Theme.shapes.small)
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { onReasonSelected(reason) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Theme.colors.primary,
                                unselectedColor = Theme.colors.hint,
                            ),
                        )
                        BasicText(
                            text = cancellationReasonLabel(reason),
                            style = Theme.typography.body.medium.copy(
                                color = Theme.colors.primaryFont,
                            ),
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            CustomTextField(
                text = note,
                onTextChange = onNoteChange,
                title = stringResource(R.string.cancel_request_detail_label),
                hint = stringResource(R.string.cancel_request_detail_hint),
                minLines = 3,
                fieldHeight = 90.dp,
                fieldVerticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SecondaryButton(
                    caption = stringResource(R.string.cancel_request_dismiss),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    isDisabled = isSubmitting,
                )
                PrimaryButton(
                    caption = stringResource(R.string.cancel_request_confirm),
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    containerColor = Theme.colors.error,
                    contentColor = Theme.colors.onError,
                    isDisabled = selectedReason == null || isSubmitting,
                    isLoading = isSubmitting,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CancelRequestDialogPreview() {
    SpTheme {
        CancelRequestDialog(
            selectedReason = CancellationReason.VEHICLE_ISSUE,
            onReasonSelected = {},
            note = "",
            onNoteChange = {},
            onDismiss = {},
            onConfirm = {},
        )
    }
}
