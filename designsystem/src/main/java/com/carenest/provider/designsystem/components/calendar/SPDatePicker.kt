package com.carenest.provider.designsystem.components.calendar

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.carenest.provider.designsystem.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SPDatePickerDialog(
    state: DatePickerState,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val datePickerColors = DatePickerDefaults.colors(
        containerColor = Theme.colors.surface,
        titleContentColor = Theme.colors.secondaryFont,
        headlineContentColor = Theme.colors.primary,
        weekdayContentColor = Theme.colors.secondaryFont,
        subheadContentColor = Theme.colors.primaryFont,
        navigationContentColor = Theme.colors.primary,
        yearContentColor = Theme.colors.primaryFont,
        disabledYearContentColor = Theme.colors.onDisable,
        currentYearContentColor = Theme.colors.primary,
        selectedYearContentColor = Theme.colors.onPrimary,
        disabledSelectedYearContentColor = Theme.colors.onDisable,
        selectedYearContainerColor = Theme.colors.primary,
        disabledSelectedYearContainerColor = Theme.colors.disable,
        dayContentColor = Theme.colors.primaryFont,
        disabledDayContentColor = Theme.colors.onDisable,
        selectedDayContentColor = Theme.colors.onPrimary,
        disabledSelectedDayContentColor = Theme.colors.onDisable,
        selectedDayContainerColor = Theme.colors.primary,
        disabledSelectedDayContainerColor = Theme.colors.disable,
        todayContentColor = Theme.colors.primary,
        todayDateBorderColor = Theme.colors.primary,
        dayInSelectionRangeContentColor = Theme.colors.onPrimaryContainer,
        dayInSelectionRangeContainerColor = Theme.colors.primaryContainer,
        dividerColor = Theme.colors.divider,
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = Theme.colors.primary)
            ) {
                Text(
                    text = stringResource(id = android.R.string.ok),
                    style = Theme.typography.body.small
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(contentColor = Theme.colors.hint)
            ) {
                Text(
                    text = stringResource(id = android.R.string.cancel),
                    style = Theme.typography.body.small
                )
            }
        },
        shape = Theme.shapes.extraLarge,
        colors = datePickerColors,
        modifier = modifier
    ) {
        DatePicker(
            state = state,
            colors = datePickerColors,
            showModeToggle = false
        )
    }
}
