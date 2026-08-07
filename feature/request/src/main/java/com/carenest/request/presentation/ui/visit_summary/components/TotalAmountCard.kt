package com.carenest.request.presentation.ui.visit_summary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R

@Composable
fun TotalAmountCard(amount: Double?, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.surface, RoundedCornerShape(16.dp))
            .padding(Theme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.visit_completed_total_amount_label),
                style = Theme.typography.hint.large.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Theme.colors.primary,
            )
            Text(
                text = amount?.let {
                    stringResource(R.string.visit_completed_total_amount_value, it)
                } ?: stringResource(R.string.not_available),
                style = Theme.typography.hint.large.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Theme.colors.primary,
            )
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = Theme.spacing.space6, vertical = Theme.spacing.space10))
    }
}

@Preview
@Composable
private fun Preview(){
    SpTheme {
        TotalAmountCard(amount = 100.0)
    }
    }
