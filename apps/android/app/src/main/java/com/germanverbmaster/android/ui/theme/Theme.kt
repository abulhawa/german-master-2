package com.germanverbmaster.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AccentLight,
    onPrimary = Color.White,
    primaryContainer = AccentContainerLight,
    onPrimaryContainer = OnLight,
    secondary = OnLightMuted,
    onSecondary = Color.White,
    secondaryContainer = SurfaceVariantLight,
    onSecondaryContainer = OnLight,
    tertiary = OnLightMuted,
    onTertiary = Color.White,
    tertiaryContainer = SurfaceVariantLight,
    onTertiaryContainer = OnLight,
    background       = SurfaceLight,
    surface          = CardLight,
    surfaceVariant = SurfaceVariantLight,
    onSurface        = OnLight,
    onSurfaceVariant = OnLightMuted,
    onBackground     = OnLight,
    outline          = BorderLight,
    outlineVariant = OutlineVariantLight,
    error = ErrorLight,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentDark,
    onPrimary = Color(0xFF0F1F30),
    primaryContainer = AccentContainerDark,
    onPrimaryContainer = OnDark,
    secondary = OnDarkMuted,
    onSecondary = Color(0xFF1A2532),
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = OnDark,
    tertiary = OnDarkMuted,
    onTertiary = Color(0xFF1A2532),
    tertiaryContainer = SurfaceVariantDark,
    onTertiaryContainer = OnDark,
    background       = SurfaceDark,
    surface          = CardDark,
    surfaceVariant = SurfaceVariantDark,
    onSurface        = OnDark,
    onSurfaceVariant = OnDarkMuted,
    onBackground     = OnDark,
    outline          = BorderDark,
    outlineVariant = OutlineVariantDark,
    error = ErrorDark,
    onError = Color(0xFF421F1F),
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
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
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content,
    )
}
