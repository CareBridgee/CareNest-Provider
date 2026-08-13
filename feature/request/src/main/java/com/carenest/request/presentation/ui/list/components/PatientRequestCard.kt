package com.carenest.request.presentation.ui.list.components

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.model.RequestStatus
import com.carenest.provider.designsystem.R as RD

@Composable
fun PatientRequestCard(
    request: Request,
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
            .clickable(onClick = onClick),
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
                    AsyncImage(
                        model = request.patientImage.ifBlank { null },
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Theme.colors.tint),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(RD.drawable.patient_imgae),
                        error = painterResource(RD.drawable.patient_imgae),
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
                                painter = painterResource(RD.drawable.ic_location),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = request.patientAddress,
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
                            R.string.nurse_requests_rate_per_hour, request.basePrice
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = request.serviceImage.ifBlank { null },
                    placeholder = painterResource(RD.drawable.ic_service_placeholder),
                    error = painterResource(RD.drawable.ic_service_placeholder),
                    contentDescription = request.serviceName,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = request.serviceName,
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = Theme.typography.body.small.fontSize,
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )

                Image(
                    painter = painterResource(RD.drawable.ic_file),
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
                        currentPrice = request.basePrice,
                        minPrice = 50f,
                        maxPrice = 120f,
                        onCancelClick = onClick,
                        onSaveClick = { updatedPrice ->
                            onEditClick()
                        })

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PatientRequestCardPreview() {
    SpTheme {
        PatientRequestCard(
            request = Request(
                id = "1",
                patientName = "Anonymous Patient",
                serviceName = "Home Care",
                serviceImage = "",
                basePrice = 120f,
                patientAddress = "",
                status = RequestStatus.ESTIMATED,
            ),
            onClick = {},
            onEditClick = {},
            onMakeOfferClick = {},
            isExpanded = false,
        )
    }
}
