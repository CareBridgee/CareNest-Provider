package com.carenest.provider.designsystem.components.request

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.bottomsheet.BaseBottomSheet
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRateBottomSheet(
    currentRate: Float,
    minRate: Float,
    maxRate: Float,
    onRateChange: (Float) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.nurse_requests_edit_rate_title),
        closeIcon = null,
        footer = {
            PrimaryButton(
                caption = stringResource(R.string.nurse_requests_save),
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.medium),
        ) {
            BasicText(
                text = stringResource(R.string.nurse_requests_edit_rate_subtitle),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                ),
            )
            Spacer(Modifier.height(24.dp))
            BasicText(
                text = stringResource(R.string.nurse_requests_rate_per_hour, currentRate),
                style = Theme.typography.displayMedium.copy(
                    color = Theme.colors.tint,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(16.dp))
            Slider(
                value = currentRate,
                onValueChange = onRateChange,
                valueRange = minRate..maxRate,
                colors = SliderDefaults.colors(
                    thumbColor = Theme.colors.tint,
                    activeTrackColor = Theme.colors.tint,
                    inactiveTrackColor = Theme.colors.track,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun MakeOfferDialog(
    countdownSeconds: Int,
    isSuccess: Boolean,
    onDismiss: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSuccess) 1f else 0.92f,
        animationSpec = tween(400),
        label = "offerDialogScale",
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Theme.colors.surface)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isSuccess) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(Theme.colors.successContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = "\u2713",
                        style = Theme.typography.displayMedium.copy(
                            color = Theme.colors.onSuccessContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                        ),
                    )
                }
                Spacer(Modifier.height(20.dp))
                BasicText(
                    text = stringResource(R.string.nurse_requests_offer_success_title),
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                )
                Spacer(Modifier.height(8.dp))
                BasicText(
                    text = stringResource(R.string.nurse_requests_offer_success_message),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        textAlign = TextAlign.Center,
                    ),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(Theme.colors.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = stringResource(
                            R.string.nurse_requests_countdown_seconds,
                            countdownSeconds,
                        ),
                        style = Theme.typography.displayMedium.copy(
                            color = Theme.colors.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                        ),
                    )
                }
                Spacer(Modifier.height(20.dp))
                BasicText(
                    text = stringResource(R.string.nurse_requests_make_offer_title),
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                )
                Spacer(Modifier.height(8.dp))
                BasicText(
                    text = stringResource(R.string.nurse_requests_make_offer_message),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview(){
    SpTheme {
        EditRateBottomSheet(
            currentRate = 85f,
            minRate = 50f,
            maxRate = 150f,
            onRateChange = {},
            onSave = {},
            onDismiss = {},
        )
    }
}
