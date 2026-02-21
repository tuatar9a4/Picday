package com.devd.commonsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.R

// Set of Material typography styles to start with
val OneDayTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.suit_extra_bold)),
        fontWeight = FontWeight(700),
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.suit_semi_bold)),
        fontWeight = FontWeight(600),
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.suit_semi_bold)),
        fontWeight = FontWeight(600),
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.suit_medium)),
        fontWeight = FontWeight(500),
        fontSize = 14.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.suit_medium)),
        fontWeight = FontWeight(500),
        fontSize = 12.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.suit_semi_bold)),
        fontWeight = FontWeight(600),
        fontSize = 11.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.suit_light)),
        fontWeight = FontWeight(400),
        fontSize = 11.sp,
    ),
)

val textHashTagStyle = TextStyle(
    fontFamily = FontFamily(Font(R.font.suit_semi_bold)),
    fontWeight = FontWeight(600),
    fontStyle = FontStyle.Italic,
    fontSize = 11.sp,
)

val LocalCustomTypography = staticCompositionLocalOf {
    OneDayTypography
}