package com.carenest.provider.designsystem.components.bottomnav

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

data class BottomNavItem(
    val label: String,
    val iconRes: Int,
    val selectedIconRes: Int = iconRes,
)

val LocalBottomNavigationContentPadding = staticCompositionLocalOf { 0.dp }

@Composable
fun SPBottomNavigation(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (Theme.isDarkTheme) {
        Theme.colors.primaryContainer
    } else {
        Theme.colors.surface
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(1f)
            .navigationBarsPadding()
            .padding(
                horizontal = Theme.spacing.medium,
                vertical = Theme.spacing.small,
            ),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            color = containerColor,
            tonalElevation = 0.dp,
            shadowElevation = 16.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .selectableGroup()
                    .padding(
                        horizontal = Theme.spacing.small,
                        vertical = Theme.spacing.small,
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, item ->
                    SPBottomNavigationItem(
                        item = item,
                        isSelected = index == selectedIndex,
                        onClick = { onItemSelected(index) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SPBottomNavigationItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedBackground = if (Theme.isDarkTheme) {
        Theme.colors.onPrimaryVariant
    } else {
        Theme.colors.primaryContainer
    }
    val activeColor = Theme.colors.primary
    val inactiveColor = Theme.colors.secondaryFont
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) selectedBackground else Color.Transparent,
        animationSpec = tween(durationMillis = 180),
        label = "Bottom navigation item background",
    )
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        animationSpec = tween(durationMillis = 160),
        label = "Bottom navigation icon tint",
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        animationSpec = tween(durationMillis = 160),
        label = "Bottom navigation label color",
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(backgroundColor)
            .selectable(
                selected = isSelected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(
                horizontal = Theme.spacing.extraSmall,
                vertical = Theme.spacing.small,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Crossfade(
            targetState = isSelected,
            animationSpec = tween(durationMillis = 140),
            label = "Bottom navigation icon",
        ) { selected ->
            Icon(
                painter = painterResource(
                    if (selected) item.selectedIconRes else item.iconRes,
                ),
                contentDescription = item.label,
                tint = iconTint,
                modifier = Modifier.size(Theme.size.iconMedium),
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        BasicText(
            text = item.label,
            maxLines = 1,
            style = Theme.typography.hint.large.copy(
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            ),
        )
    }
}

@Composable
private fun BottomNavigationPreview(
    isDarkTheme: Boolean,
    selectedIndex: Int,
) {
    SpTheme(isDarkTheme = isDarkTheme) {
        val items = remember {
            listOf(
                BottomNavItem(
                    label = "Home",
                    iconRes = R.drawable.ic_home,
                    selectedIconRes = R.drawable.ic_home_selected,
                ),
                BottomNavItem(
                    label = "Active",
                    iconRes = R.drawable.ic_work_outline,
                    selectedIconRes = R.drawable.ic_work,
                ),
                BottomNavItem(
                    label = "Earnings",
                    iconRes = R.drawable.ic_wallet_outline,
                    selectedIconRes = R.drawable.ic_wallet,
                ),
                BottomNavItem(
                    label = "Profile",
                    iconRes = R.drawable.ic_profile,
                    selectedIconRes = R.drawable.ic_profile_selected,
                ),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.backGround),
            contentAlignment = Alignment.BottomCenter,
        ) {
            SPBottomNavigation(
                items = items,
                selectedIndex = selectedIndex,
                onItemSelected = {},
            )
        }
    }
}

@Preview(
    name = "Light - Home selected",
    widthDp = 420,
    heightDp = 116,
    showBackground = true,
)
@Composable
private fun LightHomeSelectedPreview() {
    BottomNavigationPreview(isDarkTheme = false, selectedIndex = 0)
}

@Preview(
    name = "Light - Active selected",
    widthDp = 420,
    heightDp = 116,
    showBackground = true,
)
@Composable
private fun LightActiveSelectedPreview() {
    BottomNavigationPreview(isDarkTheme = false, selectedIndex = 1)
}

@Preview(
    name = "Dark - Home selected",
    widthDp = 420,
    heightDp = 116,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun DarkHomeSelectedPreview() {
    BottomNavigationPreview(isDarkTheme = true, selectedIndex = 0)
}

@Preview(
    name = "Dark - Active selected",
    widthDp = 420,
    heightDp = 116,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun DarkActiveSelectedPreview() {
    BottomNavigationPreview(isDarkTheme = true, selectedIndex = 1)
}
