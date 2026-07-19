package com.carenest.provider.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R

internal val defaultFontFamily: FontFamily
    get() = FontFamily(
        Font(R.font.cairo_extralight, FontWeight.ExtraLight),
        Font(R.font.cairo_light, FontWeight.Light),
        Font(R.font.cairo_regular, FontWeight.Normal),
        Font(R.font.cairo_medium, FontWeight.Medium),
        Font(R.font.cairo_semibold, FontWeight.SemiBold),
        Font(R.font.cairo_bold, FontWeight.Bold),
        Font(R.font.cairo_extrabold, FontWeight.ExtraBold),
        Font(R.font.cairo_black, FontWeight.Black)
    )

internal val englishFontFamily: FontFamily
    get() = FontFamily(
        Font(R.font.poppins_bold, FontWeight.ExtraLight),
        Font(R.font.poppins_regular, FontWeight.Light),
        Font(R.font.poppins_regular, FontWeight.Normal),
        Font(R.font.poppins_medium, FontWeight.Medium),
        Font(R.font.poppins_bold, FontWeight.SemiBold),
        Font(R.font.poppins_bold, FontWeight.Bold),
        Font(R.font.poppins_bold, FontWeight.ExtraBold),
        Font(R.font.poppins_bold, FontWeight.Black)
    )

data class SPTextStyle(
    val display: TextStyle,
    val title: TextStyle,
    val body: SizedTextStyle,
    val hint: SizedTextStyle,
    val displayMedium: TextStyle
)

internal val LocalSPTypography = staticCompositionLocalOf<SPTextStyle> {
    error("No typography provided")
}

internal val LocalSPFontFamily = staticCompositionLocalOf<FontFamily> {
    error("No font family provided")
}

data class SizedTextStyle(
    val large: TextStyle,
    val medium: TextStyle,
    val small: TextStyle
)

@Composable
internal fun spTypographyOf(fontFamily: FontFamily): SPTextStyle = SPTextStyle(
    // headline-lg
    display = TextStyle(
        fontFamily = fontFamily,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 38.sp,
        letterSpacing = (-0.02).sp
    ),
    // headline-lg-mobile
    displayMedium = TextStyle(
        fontFamily = fontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp,
        letterSpacing = (-0.01).sp
    ),
    // headline-md
    title = TextStyle(
        fontFamily = fontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp
    ),
    body = SizedTextStyle(
        // body-lg
        large = TextStyle(
            fontFamily = fontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 26.sp
        ),
        // body-md
        medium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp
        ),
        // label-md (used for secondary small text)
        small = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
            letterSpacing = 0.05.sp
        )
    ),
    hint = SizedTextStyle(
        // caption
        large = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp
        ),
        medium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp
        ),
        small = TextStyle(
            fontFamily = fontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 14.sp
        )
    )
)

internal fun fontFamilyForLanguage(languageCode: String): FontFamily =
    if (normalizeLanguageCode(languageCode) == "ar") defaultFontFamily else englishFontFamily

@Composable
internal fun defaultSPTypographyForLanguage(languageCode: String): SPTextStyle =
    spTypographyOf(fontFamily = fontFamilyForLanguage(languageCode))
