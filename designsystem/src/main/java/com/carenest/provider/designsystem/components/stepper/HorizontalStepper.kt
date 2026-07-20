package com.carenest.provider.designsystem.components.stepper


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

/**
 * Horizontal progress indicator: a row of circles connected by lines, with optional titles.
 *
 * - The current step is a filled donut in [Theme.colors.primary].
 * - Completed steps (before current) and the lines before the current step use
 *   [Theme.colors.primary] as outlined rings / solid lines.
 * - Upcoming steps (after current) and the lines after the current step use
 *   [Theme.colors.secondary].
 *
 * @param currentStep 1-based index of the active step.
 * @param steps list of step titles.
 */
@Composable
fun HorizontalStepper(
    currentStep: Int,
    steps: List<String>,
    modifier: Modifier = Modifier,
    showStepLabel: Boolean = true,
) {
    val totalSteps = steps.size
    Column(modifier = modifier.fillMaxWidth()) {
        if (showStepLabel) {
            BasicText(
                text = stringResource(
                    com.carenest.provider.designsystem.R.string.stepper_label,
                    currentStep,
                    totalSteps
                ),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.primaryVariant,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                ),
                modifier = Modifier.padding(bottom = Theme.spacing.small),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            steps.forEachIndexed { index, title ->
                val stepIndex = index + 1
                StepItem(
                    index = stepIndex,
                    currentStep = currentStep,
                    title = title,
                    isLast = stepIndex == totalSteps,
                    modifier = if (stepIndex != totalSteps) Modifier.weight(1f) else Modifier
                )
            }
        }
    }
}

@Composable
private fun RowScope.StepItem(
    index: Int,
    currentStep: Int,
    title: String,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            StepCircle(index = index, currentStep = currentStep)
            Spacer(modifier = Modifier.height(Theme.spacing.extraSmall))
            BasicText(
                text = title,
                style = Theme.typography.body.small.copy(
                    color = if (index <= currentStep) Theme.colors.primaryFont else Theme.colors.secondaryFont,
                    fontWeight = if (index == currentStep) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
        }

        if (!isLast) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Theme.spacing.extraSmall)
                    .padding(top = 11.dp)
                    .height(2.dp)
                    .background(
                        if (index < currentStep) Theme.colors.primary
                        else Theme.colors.secondary,
                    ),
            )
        }
    }
}

@Composable
private fun StepCircle(index: Int, currentStep: Int) {
    val circleSize = 24.dp
    when {
        index == currentStep -> {
            // Filled donut: solid primary disc with a light center dot.
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(Theme.colors.primary),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.backGround),
                )
            }
        }

        index < currentStep -> {
            // Completed: primary ring.
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .border(2.dp, Theme.colors.primary, CircleShape),
            )
        }

        else -> {
            // Upcoming: secondary ring.
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .border(2.dp, Theme.colors.secondary, CircleShape),
            )
        }
    }
}

@Preview
@Composable
private fun HorizontalStepperPreview() {
    val steps = listOf("Info", "Docs", "Services", "Review")
    SpTheme(isDarkTheme = false, languageCode = "en") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.backGround)
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.large),
        ) {
            HorizontalStepper(currentStep = 1, steps = steps)
            HorizontalStepper(currentStep = 2, steps = steps)
            HorizontalStepper(currentStep = 4, steps = steps)
        }
    }
}
