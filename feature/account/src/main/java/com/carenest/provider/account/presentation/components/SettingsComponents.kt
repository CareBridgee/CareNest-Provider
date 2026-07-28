package com.carenest.provider.account.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.switch.SPSwitch
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun SettingsNavigationRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    value: String? = null,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = Theme.spacing.medium, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Theme.colors.secondaryFont)
        Spacer(Modifier.width(Theme.spacing.medium))
        BasicText(
            text = title,
        style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont),
            modifier = Modifier.weight(1f),
        )
        if (value != null) {
            BasicText(
                text = value,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Normal,
                ),
            )
            Spacer(Modifier.width(Theme.spacing.extraSmall))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = Theme.colors.hint,
            modifier = Modifier.size(26.dp),
        )
    }
}

@Composable
fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = Theme.spacing.medium, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Theme.colors.secondaryFont)
        Spacer(Modifier.width(Theme.spacing.medium))
        BasicText(
            text = title,
            style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont),
            modifier = Modifier.weight(1f),
        )
        SPSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
