package com.carenest.request.presentation.ui.details.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.R as RD
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.domain.model.Offer

@Composable
fun ServiceSection(
    offer: Offer,
    onViewSummaryClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    BasicText(
        text = stringResource(R.string.request_details_service_section),
        style = Theme.typography.body.small.copy(color = Theme.colors.primary, fontWeight = FontWeight.Bold),
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicText(
                text = offer.serviceType,
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                painter = painterResource(RD.drawable.ic_bag_success),
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            BasicText(
                text = stringResource(RD.string.request_details_estimated_duration),
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont, fontWeight = FontWeight.Normal),
            )
            BasicText(
                text = offer.estimatedDuration,
                style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont, fontWeight = FontWeight.Bold),
            )
        }
    }

    Spacer(Modifier.height(Theme.spacing.small))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.small)
            .background(Theme.colors.backGround)
            .clickable { onViewSummaryClicked() }
            .padding(Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            text = stringResource(RD.string.request_details_view_summary),
            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(RD.drawable.ic_document_text),
            contentDescription = null,
            tint = Theme.colors.secondaryFont,
            modifier = Modifier.size(20.dp)
        )
    }
}