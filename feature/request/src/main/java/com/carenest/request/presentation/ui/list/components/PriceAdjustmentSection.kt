package com.carenest.request.presentation.ui.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import kotlin.math.roundToInt

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