package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun createAccessibleTypography(
    scale: Float = 1.0f,
    dyslexiaSpacing: Boolean = false
): Typography {
    val letterSpacing = if (dyslexiaSpacing) (0.8 * scale).sp else (0.3 * scale).sp
    val lineHeightMultiplier = if (dyslexiaSpacing) 1.5f else 1.35f

    return Typography(
        displayLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = (32 * scale).sp,
            lineHeight = (40 * scale * lineHeightMultiplier / 1.35f).sp,
            letterSpacing = letterSpacing
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (28 * scale).sp,
            lineHeight = (36 * scale * lineHeightMultiplier / 1.35f).sp,
            letterSpacing = letterSpacing
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = (24 * scale).sp,
            lineHeight = (32 * scale * lineHeightMultiplier / 1.35f).sp,
            letterSpacing = letterSpacing
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20 * scale).sp,
            lineHeight = (28 * scale * lineHeightMultiplier / 1.35f).sp,
            letterSpacing = letterSpacing
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = (18 * scale).sp,
            lineHeight = (26 * scale * lineHeightMultiplier / 1.35f).sp,
            letterSpacing = letterSpacing
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (16 * scale).sp,
            lineHeight = (24 * scale * lineHeightMultiplier / 1.35f).sp,
            letterSpacing = letterSpacing
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * scale).sp,
            lineHeight = (24 * scale * lineHeightMultiplier / 1.35f).sp,
            letterSpacing = letterSpacing
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * scale).sp,
            lineHeight = (20 * scale * lineHeightMultiplier / 1.35f).sp,
            letterSpacing = letterSpacing
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (14 * scale).sp,
            lineHeight = (20 * scale * lineHeightMultiplier / 1.35f).sp,
            letterSpacing = letterSpacing
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = (12 * scale).sp,
            lineHeight = (16 * scale * lineHeightMultiplier / 1.35f).sp,
            letterSpacing = letterSpacing
        )
    )
}

val DefaultTypography = createAccessibleTypography(1.0f, false)
