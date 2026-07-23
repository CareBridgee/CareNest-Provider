package com.carenest.home.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.carenest.home.R
import com.carenest.home.domain.model.NurseRequest
import com.carenest.home.domain.model.RequestStatus
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import kotlin.math.roundToInt

@Composable
fun NurseRequestCard(
    request: NurseRequest,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onMakeOfferClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val isInteractive = request.status == RequestStatus.ESTIMATED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isExpanded) 4.dp else 2.dp,
                shape = Theme.shapes.large,
                clip = false,
            )
            .animateContentSize(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .then(
                if (isInteractive) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            ),
        shape = Theme.shapes.large,
        border = BorderStroke(
            width = 1.dp,
            color = Theme.colors.tint.copy(
                alpha = if (request.status == RequestStatus.ACCEPTED) 0.28f else 0.16f,
            ),
        ),
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.medium)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically
                ) {
                    //will use async image
                    Image(
                        painter = painterResource(com.carenest.provider.designsystem.R.drawable.patient_imgae),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Theme.colors.tint),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(Theme.spacing.small))

                    Column {

                        Text(
                            text = request.patientName,
                            style = Theme.typography.body.medium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Theme.colors.primaryFont,
                            ),
                            maxLines = 2,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                painter = painterResource(com.carenest.provider.designsystem.R.drawable.ic_location),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(
                                    R.string.nurse_requests_distance_miles, request.distanceMiles
                                ),
                                style = Theme.typography.body.small.copy(
                                    color = Theme.colors.secondaryFont,
                                ),
                            )
                        }

                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(
                            R.string.nurse_requests_rate_per_hour, request.baseRate
                        ), style = Theme.typography.title.copy(
                            color = Theme.colors.tint,
                            fontWeight = FontWeight.SemiBold,
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    RequestStatusBadge(
                        status = request.status
                    )
                }
            }

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(
                        color = Theme.colors.disable.copy(alpha = 0.30f),
                        shape = Theme.shapes.medium,
                    )
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalAlignment = Alignment.CenterVertically)
            {
                AsyncImage(
                    model = request.serviceImage.ifBlank { null },
                    placeholder = painterResource(com.carenest.provider.designsystem.R.drawable.ic_service_placeholder),
                    error = painterResource(com.carenest.provider.designsystem.R.drawable.ic_service_placeholder),
                    contentDescription = request.serviceType,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = request.serviceType, style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = Theme.typography.body.small.fontSize,
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )

                Image(
                    painter = painterResource(com.carenest.provider.designsystem.R.drawable.ic_file),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

            }

            AnimatedVisibility(
                visible = isExpanded && isInteractive,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(Theme.spacing.medium))

                    HorizontalDivider(color = Theme.colors.track)

                    Spacer(modifier = Modifier.height(Theme.spacing.medium))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                    ) {
                        SecondaryButton(
                            caption = stringResource(R.string.nurse_requests_action_edit),
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f),
                        )

                        PrimaryButton(
                            caption = stringResource(R.string.nurse_requests_action_make_offer),
                            onClick = onMakeOfferClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = request.status == RequestStatus.ACCEPTED,
                enter = expandVertically() + fadeIn()
            ) {

                Column {

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        color = Theme.colors.track
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PriceAdjustmentSection(
                        currentPrice = request.baseRate,
                        minPrice = 50f,
                        maxPrice = 120f,
                        onCancelClick = onClick,
                        onSaveClick = { updatedPrice ->
                            // Handle saving updated rate here
                            onEditClick()
                        }
                    )

                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceAdjustmentSection(
    currentPrice: Float,
    minPrice: Float = 50f,
    maxPrice: Float = 120f,
    onCancelClick: () -> Unit,
    onSaveClick: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPrice by remember(currentPrice) { mutableFloatStateOf(currentPrice) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                .background(Theme.colors.surface, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$${selectedPrice.roundToInt()}.00",
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stringResource(R.string.estimated_price),
                    style = Theme.typography.hint.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = selectedPrice,
            onValueChange = { selectedPrice = it },
            valueRange = minPrice..maxPrice,
            colors = SliderDefaults.colors(
                thumbColor = Theme.colors.primary,
                activeTrackColor = Theme.colors.primary,
                inactiveTrackColor = Theme.colors.disable
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$${minPrice.toInt()} ${stringResource(R.string.min)}",
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont
                )
            )
            Text(
                text = "$${maxPrice.toInt()} ${stringResource(R.string.Max)}",
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryButton(
                caption = stringResource(R.string.cancel),
                onClick = onCancelClick,
                modifier = Modifier.weight(1f)
            )

            PrimaryButton(
                caption = stringResource(R.string.save),
                onClick = { onSaveClick(selectedPrice) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NurseRequestCardPreview() {
    SpTheme {
        NurseRequestCard(
            request = NurseRequest(
                id = "1",
                patientName = "Anonymous Patient",
                patientImage = "",
                serviceType = "Injection Service",
                baseRate = 85f,
                distanceMiles = 2.4f,
                status = RequestStatus.ESTIMATED,
                progressStep = 0,
                serviceImage = ""
            ),
            isExpanded = true,
            onClick = {},
            onEditClick = {},
            onMakeOfferClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NurseRequestAcceptedPreview() {
    SpTheme {
        NurseRequestCard(
            request = NurseRequest(
                id = "2",
                patientName = "Anonymous Patient",
                patientImage = "",
                serviceType = "Home Care",
                baseRate = 120f,
                distanceMiles = 1.8f,
                status = RequestStatus.ACCEPTED,
                progressStep = 2,
                serviceImage = ""
            ),
            isExpanded = false,
            onClick = {},
            onEditClick = {},
            onMakeOfferClick = {},
        )
    }
}
