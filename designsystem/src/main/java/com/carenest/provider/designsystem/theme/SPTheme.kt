package com.carenest.provider.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
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

internal val LocalIsDarkTheme = staticCompositionLocalOf { false }

internal fun normalizeLanguageCode(languageCode: String): String =
    languageCode
        .trim()
        .lowercase()
        .substringBefore('-')
        .substringBefore('_')

private fun isRtlLanguage(languageCode: String): Boolean =
    languageCode in setOf("ar", "fa", "he", "iw", "ur")
