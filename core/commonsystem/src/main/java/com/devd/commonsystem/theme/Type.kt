package com.devd.commonsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.utils.FontList

fun getCustomTypography(fontFamily: FontFamily): Typography {
    return Typography(
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 18.sp
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 18.sp
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(500),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 18.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 14.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(500),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 14.sp
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 14.sp
        ),
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 12.sp
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 12.sp
        ),
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(500),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 12.sp
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 11.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 11.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),
            fontSize = 9.sp,
        )
    )

}

val LocalCustomTypography = compositionLocalOf {
    getCustomTypography(FontList.SUIT.fontFamily)
}