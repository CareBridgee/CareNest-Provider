package com.carenest.request.presentation.ui.details.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R as RD
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.domain.model.Offer

@Composable
fun PaymentSection(
    offer: Offer,
    modifier: Modifier = Modifier
){
    BasicText(
        text = stringResource(R.string.request_details_payment_section),
        style = Theme.typography.body.small.copy(color = Theme.colors.primary, fontWeight = FontWeight.Bold),
    )
    HorizontalDivider(modifier = modifier.padding(vertical = Theme.spacing.small))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            text = stringResource(R.string.request_details_total_amount),
            style = Theme.typography.body.large.copy(color = Theme.colors.primaryFont, fontWeight = FontWeight.Bold),
        )
        BasicText(
            text = stringResource(R.string.request_details_amount_value, offer.totalAmount),
            style = Theme.typography.title.copy(color = Theme.colors.primary, fontWeight = FontWeight.Bold),
        )
    }

    Spacer(Modifier.height(Theme.spacing.small))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.small)
            .background(Theme.colors.backGround)
            .padding(Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(RD.drawable.ic_payment_method),
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(Theme.spacing.small))
        BasicText(
            text = stringResource(R.string.request_details_payment_info),
            style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont, fontWeight = FontWeight.Normal, lineHeight = 16.sp),
            modifier = Modifier.weight(1f)
        )
    }
}