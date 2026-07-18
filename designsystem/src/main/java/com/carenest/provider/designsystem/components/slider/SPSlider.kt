package com.carenest.provider.designsystem.components.slider

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun SPSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Theme.colors.tint,
            inactiveTrackColor = Theme.colors.track,
            disabledThumbColor = Color.White.copy(alpha = 0.5f),
            disabledActiveTrackColor = Theme.colors.tint.copy(alpha = 0.5f),
            disabledInactiveTrackColor = Theme.colors.track.copy(alpha = 0.5f)
        )
    )
}
