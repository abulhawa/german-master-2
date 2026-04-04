package com.germanverbmaster.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary          = Brand500,
    onPrimary        = BrandOnPrimary,
    primaryContainer = Brand400,
    background       = SurfaceLight,
    surface          = CardLight,
    onSurface        = FgLight,
    onBackground     = FgLight,
    outline          = BorderLight,
    secondary        = Brand400,
    onSecondary      = BrandOnPrimary,
    error            = Danger,
)

private val DarkColorScheme = darkColorScheme(
    primary          = Brand400,
    onPrimary        = BrandOnPrimary,
    primaryContainer = Brand600,
    background       = SurfaceDark,
    surface          = CardDark,
    onSurface        = FgDark,
    onBackground     = FgDark,
    outline          = BorderDark,
    secondary        = Brand400,
    onSecondary      = BrandOnPrimary,
    error            = Danger,
)

@Composable
fun GermanVerbMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content,
    )
}
