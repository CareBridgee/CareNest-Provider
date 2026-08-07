package com.carenest.request.presentation.ui.details.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.domain.model.Offer
import com.carenest.request.presentation.ui.components.ContactActionsRow
import com.carenest.provider.designsystem.R as RD

@Composable
fun PatientSection(
    offer: Offer, onCallClick: () -> Unit,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = offer.patientInfo.image,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp)),
            placeholder = painterResource(RD.drawable.patient),
            error = painterResource(RD.drawable.patient),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(Theme.spacing.medium))
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = offer.patientInfo.name,
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )
            BasicText(
                text = offer.patientInfo.age?.let {
                    stringResource(R.string.request_details_patient_age, it)
                } ?: stringResource(R.string.not_available),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Normal
                ),
            )
        }
        ContactActionsRow(
            onCallClick = onCallClick, onMessageClick = onMessageClick
        )
    }
}
