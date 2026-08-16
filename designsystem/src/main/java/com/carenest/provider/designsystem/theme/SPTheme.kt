package com.carenest.provider.designsystem.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.carenest.provider.designsystem.dimensions.LocalSPShapes
import com.carenest.provider.designsystem.dimensions.LocalSPSize
import com.carenest.provider.designsystem.dimensions.LocalSPSpacing
import com.carenest.provider.designsystem.dimensions.SPShapes
import com.carenest.provider.designsystem.dimensions.SPSize
import com.carenest.provider.designsystem.dimensions.SPSpacing

@Composable
fun SpTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    languageCode: String = "en",
    content: @Composable () -> Unit,
) {
    val normalizedLanguage = remember(languageCode) { normalizeLanguageCode(languageCode) }
    val colors = remember(isDarkTheme) {
        if (isDarkTheme) darkColors else lightColors
    }
    val fontFamily = remember(normalizedLanguage) {
        fontFamilyForLanguage(normalizedLanguage)
    }
    val typography = spTypographyOf(fontFamily)
    val layoutDirection = remember(normalizedLanguage) {
        if (isRtlLanguage(normalizedLanguage)) LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            if (activity != null) {
                val primaryArgb = colors.primary.toArgb()
                val useDarkIcons = colors.primary.luminance() > 0.5f
                val style = if (useDarkIcons) {
                    SystemBarStyle.light(primaryArgb, primaryArgb)
                } else {
                    SystemBarStyle.dark(primaryArgb)
                }
                (activity as? ComponentActivity)?.enableEdgeToEdge(
                    statusBarStyle = style,
                    navigationBarStyle = style,
                )
                val window = activity.window
                window.statusBarColor = primaryArgb
                window.navigationBarColor = primaryArgb
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = useDarkIcons
                insetsController.isAppearanceLightNavigationBars = useDarkIcons
            }
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        localSPColorScheme provides colors,
        LocalSPTypography provides typography,
        LocalSPFontFamily provides fontFamily,
        LocalSPSpacing provides remember { SPSpacing() },
        LocalSPShapes provides remember { SPShapes() },
        LocalSPSize provides remember { SPSize() },
        LocalIsDarkTheme provides isDarkTheme,
        content = content,
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

internal val LocalIsDarkTheme = staticCompositionLocalOf { false }

internal fun normalizeLanguageCode(languageCode: String): String =
    languageCode
        .trim()
        .lowercase()
        .substringBefore('-')
        .substringBefore('_')

private fun isRtlLanguage(languageCode: String): Boolean =
    languageCode in setOf("ar", "fa", "he", "iw", "ur")
