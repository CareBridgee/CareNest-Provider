package com.carenest.provider.designsystem.theme


import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.carenest.provider.designsystem.dimensions.LocalSPShapes
import com.carenest.provider.designsystem.dimensions.LocalSPSize
import com.carenest.provider.designsystem.dimensions.LocalSPSpacing
import com.carenest.provider.designsystem.dimensions.SPShapes
import com.carenest.provider.designsystem.dimensions.SPSize
import com.carenest.provider.designsystem.dimensions.SPSpacing

object Theme {
    val colors: ColorScheme
        @Composable
        get() = localSPColorScheme.current

    val typography: SPTextStyle
        @Composable
        get() = LocalSPTypography.current

    val spacing: SPSpacing
        @Composable
        get() = LocalSPSpacing.current

    val shapes: SPShapes
        @Composable
        get() = LocalSPShapes.current

    val size: SPSize
        @Composable @ReadOnlyComposable get() = LocalSPSize.current
}
