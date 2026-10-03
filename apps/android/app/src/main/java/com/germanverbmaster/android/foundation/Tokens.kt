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
        background = Color(0xFFF6F7FB),
        surface = Color(0xFFFFFFFF),
        text = Color(0xFF172033),
        secondary = Color(0xFF526078),
        primary = Color(0xFF2457C5),
        onPrimary = Color(0xFFFFFFFF),
        success = Color(0xFF176447),
        attention = Color(0xFF854D0E),
        error = Color(0xFFA62B36),
        border = Color(0xFFD7DEEA),
        controlBorder = Color(0xFF526078),
    )
    val dark = FoundationColors(
        background = Color(0xFF101826),
        surface = Color(0xFF192437),
        text = Color(0xFFF2F5FA),
        secondary = Color(0xFFB2BED0),
        primary = Color(0xFF8FB2FF),
        onPrimary = Color(0xFF101826),
        success = Color(0xFF82D7B2),
        attention = Color(0xFFF3CC84),
        error = Color(0xFFFFA4AC),
        border = Color(0xFF3C4A61),
        controlBorder = Color(0xFFB2BED0),
    )
}
