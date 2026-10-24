// Generated from design/tokens.json.
package com.germanverbmaster.android.foundation

import androidx.compose.ui.graphics.Color

data class FoundationColors(
    val background: Color,
    val surface: Color,
    val text: Color,
    val secondary: Color,
    val primary: Color,
    val onPrimary: Color,
    val success: Color,
    val attention: Color,
    val error: Color,
    val border: Color,
    val controlBorder: Color
)
object FoundationTokens {
    const val controlMin = 48
    const val practiceMax = 720
    const val radius = 16
    val spacing = listOf(4, 8, 12, 16, 24, 32, 48)
    val light = FoundationColors(
        background = Color(0xFFF7F8FA),
        surface = Color(0xFFFFFFFF),
        text = Color(0xFF18212E),
        secondary = Color(0xFF596576),
        primary = Color(0xFF304BD8),
        onPrimary = Color(0xFFFFFFFF),
        success = Color(0xFF176447),
        attention = Color(0xFF854D0E),
        error = Color(0xFFA62B36),
        border = Color(0xFFE3E7EE),
        controlBorder = Color(0xFF526078),
    )
    val dark = FoundationColors(
        background = Color(0xFF11151C),
        surface = Color(0xFF1A202A),
        text = Color(0xFFF1F4F9),
        secondary = Color(0xFFA8B4C5),
        primary = Color(0xFFA3B2FF),
        onPrimary = Color(0xFF14214D),
        success = Color(0xFF82D7B2),
        attention = Color(0xFFF3CC84),
        error = Color(0xFFFFA4AC),
        border = Color(0xFF303948),
        controlBorder = Color(0xFFB2BED0),
    )
}
