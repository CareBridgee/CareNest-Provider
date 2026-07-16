package com.carenest.provider.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ColorScheme(
    val primary: Color,
    val primaryVariant: Color,
    val onPrimary: Color,
    val onPrimaryVariant: Color,
    val secondary: Color,
    val onSecondary: Color,
    val backGround: Color,
    val primaryFont: Color,
    val secondaryFont: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val hint: Color,
    val warning: Color,
    val onWarning: Color,
    val error: Color,
    val onError: Color,
    val success: Color,
    val onSuccess: Color,
    val amber: Color,
    val onAmber: Color,
    val disable: Color,
    val onDisable: Color,
    val tint: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
    val bankContainer: Color,
    val onBankContainer: Color,
    val vodafoneContainer: Color,
    val onVodafoneContainer: Color,
    val processingContainer: Color,
    val onProcessingContainer: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val track: Color,
    val divider: Color
)

val lightColors = ColorScheme(
    primary = Color(0xFF006168),           // primary
    primaryVariant = Color(0xFF0D7C84),    // primary-container
    onPrimary = Color(0xFFFFFFFF),         // on-primary
    onPrimaryVariant = Color(0xFFD9FCFF),  // on-primary-container
    secondary = Color(0xFF516161),         // secondary
    onSecondary = Color(0xFFFFFFFF),       // on-secondary
    backGround = Color(0xFFF8F9FA),        // background
    primaryFont = Color(0xFF191C1D),       // on-surface
    secondaryFont = Color(0xFF3E494A),     // on-surface-variant
    surface = Color(0xFFFFFFFF),           // surface-container-lowest
    surfaceVariant = Color(0xFFE1E3E4),    // surface-variant
    onSurface = Color(0xFF191C1D),         // on-surface
    hint = Color(0xFF6E797A),              // outline
    warning = Color(0xFFFFC107),
    onWarning = Color(0xFF000000),
    error = Color(0xFFBA1A1A),             // error
    onError = Color(0xFFFFFFFF),           // on-error
    success = Color(0xFF43A047),           // progress-indicator success
    onSuccess = Color(0xFFFFFFFF),
    amber = Color(0xFFD97706),
    onAmber = Color(0xFFFFFFFF),
    disable = Color(0xFFEDEEEF),           // surface-container
    onDisable = Color(0xFFBDC9CA),         // outline-variant
    tint = Color(0xFF006970),
    successContainer = Color(0xFFE8F5E9),
    onSuccessContainer = Color(0xFF2E7D32),
    warningContainer = Color(0xFFFFF3E0),
    onWarningContainer = Color(0xFFEF6C00),
    primaryContainer = Color(0xFFD3E8E6),
    onPrimaryContainer = Color(0xFF004D40),
    infoContainer = Color(0xFFF5F5F5),
    onInfoContainer = Color(0xFF636369),
    bankContainer = Color(0xFFE0F2F1),
    onBankContainer = Color(0xFF004D40),
    vodafoneContainer = Color(0xFFFFF1F0),
    onVodafoneContainer = Color(0xFFD32F2F),
    processingContainer = Color(0xFFE3F2FD),
    onProcessingContainer = Color(0xFF1976D2),
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFD32F2F),
    track = Color(0xFFEBEBEB),
    divider = Color(0xFFF0F0F0)
)

val darkColors = ColorScheme(
    primary = Color(0xFF7CD4DD),           // inverse-primary
    primaryVariant = Color(0xFF71D7CD),    // tertiary-fixed-dim
    onPrimary = Color(0xFF002022),         // on-primary-fixed
    onPrimaryVariant = Color(0xFF004F54),  // on-primary-fixed-variant
    secondary = Color(0xFFB8CAC9),         // secondary-fixed-dim
    onSecondary = Color(0xFF0E1E1E),       // on-secondary-fixed
    backGround = Color(0xFF191C1D),
    primaryFont = Color(0xFFE1E3E4),
    secondaryFont = Color(0xFFBDC9CA),
    surface = Color(0xFF2E3132),           // inverse-surface
    surfaceVariant = Color(0xFF3E494A),
    onSurface = Color(0xFFF0F1F2),         // inverse-on-surface
    hint = Color(0xFF6E797A),
    warning = Color(0xFFFFD54F),
    onWarning = Color(0xFF121212),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    success = Color(0xFF81C784),
    onSuccess = Color(0xFF121212),
    amber = Color(0xFFD97706),
    onAmber = Color(0xFFFFFFFF),
    disable = Color(0xFF3E494A),
    onDisable = Color(0xFF6E797A),
    tint = Color(0xFF7CD4DD),
    successContainer = Color(0xFF1B2E1E),
    onSuccessContainer = Color(0xFF81C784),
    warningContainer = Color(0xFF2E200D),
    onWarningContainer = Color(0xFFFFD54F),
    primaryContainer = Color(0xFF1B2E2E),
    onPrimaryContainer = Color(0xFF80CBC4),
    infoContainer = Color(0xFF2C2C2C),
    onInfoContainer = Color(0xFFAAAAAA),
    bankContainer = Color(0xFF002D2D),
    onBankContainer = Color(0xFF80CBC4),
    vodafoneContainer = Color(0xFF2D0A0A),
    onVodafoneContainer = Color(0xFFE57373),
    processingContainer = Color(0xFF0D1B2D),
    onProcessingContainer = Color(0xFF64B5F6),
    errorContainer = Color(0xFF2D0A0A),
    onErrorContainer = Color(0xFFE57373),
    track = Color(0xFF2C2C2C),
    divider = Color(0xFF2C2C2C)
)

internal val localSPColorScheme = staticCompositionLocalOf { darkColors }