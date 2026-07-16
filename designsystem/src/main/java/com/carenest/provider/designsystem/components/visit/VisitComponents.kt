package com.carenest.provider.designsystem.components.visit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

// State data models
enum class StepStatus {
    Completed, InProgress, Pending
}

data class VisitStep(
    val title: String,
    val subtitle: String,
    val status: StepStatus
)

@Composable
fun VerticalVisitStepper(
    steps: List<VisitStep>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface)
    ) {
        Column(
            modifier = Modifier.padding(Theme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Timeline side
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(32.dp)
                    ) {
                        StepIndicator(status = step.status)
                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(40.dp)
                                    .background(
                                        if (step.status == StepStatus.Completed) Theme.colors.success 
                                        else Theme.colors.track
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(Theme.spacing.medium))

                    // Content side
                    Column(
                        modifier = Modifier.padding(bottom = if (index < steps.size - 1) 24.dp else 0.dp)
                    ) {
                        BasicText(
                            text = step.title,
                            style = Theme.typography.body.large.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (step.status == StepStatus.Pending) Theme.colors.hint 
                                        else if (step.status == StepStatus.InProgress) Theme.colors.onPrimaryContainer
                                        else Theme.colors.primaryFont
                            )
                        )
                        BasicText(
                            text = step.subtitle,
                            style = Theme.typography.body.small.copy(
                                color = Theme.colors.secondaryFont,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(status: StepStatus) {
    when (status) {
        StepStatus.Completed -> {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.success)
                    .padding(4.dp)
            )
        }
        StepStatus.InProgress -> {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.onPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
        StepStatus.Pending -> {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.track),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.disable)
                )
            }
        }
    }
}

@Composable
fun PatientInfoCard(
    name: String,
    address: String,
    imagePainter: Painter,
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface)
    ) {
        Row(
            modifier = Modifier.padding(Theme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = imagePainter,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(Theme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(Theme.spacing.medium))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                BasicText(
                    text = name,
                    style = Theme.typography.title.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Theme.colors.primaryFont
                    )
                )
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = null,
                        tint = Theme.colors.secondaryFont,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    BasicText(
                        text = address,
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.secondaryFont,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            IconButton(
                onClick = onCallClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primaryContainer)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_call),
                    contentDescription = "Call",
                    tint = Theme.colors.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Theme.colors.onPrimaryContainer,
    icon: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier
            .shadow(elevation = 1.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface)
    ) {
        Column(
            modifier = Modifier.padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BasicText(
                text = label,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.hint,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (icon != null) {
                    icon()
                }
                BasicText(
                    text = value,
                    style = Theme.typography.body.large.copy(
                        fontWeight = FontWeight.Bold,
                        color = valueColor,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}

@Composable
fun OngoingVisitScreen(
    steps: List<VisitStep>,
    patientName: String,
    patientAddress: String,
    patientImage: Painter,
    elapsedTime: String,
    vitalStatus: String,
    vitalColor: Color,
    onCompleteVisit: () -> Unit,
    onViewChecklist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.large)
    ) {
        // Top Section (Simplified Header)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile & Timer Mock
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Theme.colors.secondary))
                BasicText(
                    text = "00:42:17",
                    style = Theme.typography.title.copy(color = Theme.colors.onPrimaryContainer, fontSize = 22.sp)
                )
            }
            Icon(painterResource(id = R.drawable.ic_notification), null, tint = Theme.colors.onPrimaryContainer)
        }

        BasicText(
            text = stringResource(id = R.string.ongoing_visit_title),
            style = Theme.typography.title.copy(
                fontWeight = FontWeight.Bold,
                color = Theme.colors.onPrimaryContainer,
                fontSize = 24.sp
            )
        )

        VerticalVisitStepper(steps = steps)

        PatientInfoCard(
            name = patientName,
            address = patientAddress,
            imagePainter = patientImage,
            onCallClick = {}
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            MetricCard(
                label = stringResource(id = R.string.elapsed_time_label),
                value = elapsedTime,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = stringResource(id = R.string.vital_status_label),
                value = vitalStatus,
                valueColor = Theme.colors.primaryFont,
                icon = {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(vitalColor))
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            PrimaryButton(
                caption = stringResource(id = R.string.action_complete_visit),
                onClick = onCompleteVisit,
                modifier = Modifier.fillMaxWidth(),
                iconPainter = painterResource(id = R.drawable.ic_check),
                containerColor = Theme.colors.onPrimaryContainer
            )
            
            PrimaryButton(
                caption = stringResource(id = R.string.action_view_checklist),
                onClick = onViewChecklist,
                modifier = Modifier.fillMaxWidth(),
                iconPainter = painterResource(id = R.drawable.ic_assignment),
                containerColor = Theme.colors.primaryContainer,
                contentColor = Theme.colors.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OngoingVisitPreview() {
    val steps = listOf(
        VisitStep("Arrived", "09:00 AM • Verified by GPS", StepStatus.Completed),
        VisitStep("Assessment", "09:15 AM • Vitals recorded", StepStatus.Completed),
        VisitStep("Treatment in progress", "Started at 09:30 AM", StepStatus.InProgress),
        VisitStep("Post-Care Summary", "Pending completion", StepStatus.Pending)
    )

    SpTheme {
        Surface(color = Color(0xFFF8F8F8)) {
            OngoingVisitScreen(
                steps = steps,
                patientName = "Arthur Miller",
                patientAddress = "221B Baker St,\nLondon",
                patientImage = painterResource(id = R.drawable.img_placeholder), // Mock
                elapsedTime = "00:42:15",
                vitalStatus = "Stable",
                vitalColor = Color(0xFF4CAF50),
                onCompleteVisit = {},
                onViewChecklist = {}
            )
        }
    }
}
