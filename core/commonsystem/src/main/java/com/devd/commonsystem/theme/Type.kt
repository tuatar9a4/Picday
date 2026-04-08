package com.devd.commonsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.R
import com.devd.commonsystem.utils.FontList

fun getCustomTypography(fontFamily: FontFamily): Typography {
    return Typography(
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 22.sp
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            fontSize = 22.sp
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(500),
            fontSize = 22.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            fontSize = 16.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(500),
            fontSize = 16.sp
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 16.sp
        ),
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 14.sp
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            fontSize = 14.sp
        ),
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(500),
            fontSize = 14.sp
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            fontSize = 11.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 11.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 9.sp,
        )
    )

}

val textHashTagStyle = TextStyle(
    fontFamily = FontFamily(Font(R.font.suit_semi_bold)),
    fontWeight = FontWeight(600),
    fontStyle = FontStyle.Italic,
    fontSize = 11.sp,
)
val textHashTagSmallStyle = TextStyle(
    fontFamily = FontFamily(Font(R.font.suit_semi_bold)),
    fontWeight = FontWeight(600),
    fontStyle = FontStyle.Italic,
    fontSize = 9.sp,
)

val LocalCustomTypography = compositionLocalOf {
    getCustomTypography(FontList.SUIT.fontFamily)
}