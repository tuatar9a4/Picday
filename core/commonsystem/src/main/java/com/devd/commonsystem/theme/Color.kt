package com.devd.commonsystem.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


val TransParents = Color(0x00000000)

//https://kwongdevelop.tistory.com/5
val PrimaryColor = Color(0xFFF5F1EB)
val SecondaryColor = Color(0xFFC8B6A6)
val TextDefaultColor = Color(0xFF2E2E2E)
val TextOpacity80Color = Color(0xCC2E2E2E)
val AccentColor = Color(0xFF6D64D0)
val AccentOpacity40Color = Color(0x66C38EB4)
val SubAccentColor = Color(0xFFF5F4FD)
val AccentDimColor = Color(0xFF9B51E0)
val SubAccentDimColor = Color(0xFFF9F7FA)
val WhiteColor = Color(0xFFFFFFFF)
val WhiteOpacity40Color = Color(0x66FFFFFF)
val BlackColor = Color(0xFF000000)
val BlackOpacity40Color = Color(0x66000000)
val BlackOpacity30Color = Color(0x4d000000)
val BlackOpacity90Color = Color(0x1a000000)
val RedColor = Color(0xFFE3242B)
val YellowColor = Color(0xFFd6c80d)
val GreyColor = Color(0xFF8F8A84)
val GreyOpacity40Color = Color(0x668F8A84)

val Black33Color = Color(0xFF333333)
val Black33Opacity90Color = Color(0x1a333333)
val BlackF2Color = Color(0xFFF2F2F2)
val BlackD9Color = Color(0xFFD9D9D9)
val BlackDDColor = Color(0xFFDDDDDD)
val BlackF4Color = Color(0xFFF4F4F4)
val BlackF9Color = Color(0xFFF9F9F9)


/* Book Color Combination */
val ModernDarkMain = Color(0xFF121212)
val ModernDarkPoint = Color(0xFFBB86FC)
val CleanMinimalMain = Color(0xFFF8F9FA)
val CleanMinimalPoint = Color(0xFF007BFF)
val CalmNatureMain = Color(0xFFF5F5DC)
val CalmNaturePoint = Color(0xFF2D5A27)
val MidnightMain = Color(0xFF1A2238)
val MidnightPoint = Color(0xFFFFD700)
val SoftPastelMain = Color(0xFFFFF0F5)
val SoftPastelPoint = Color(0xFFFF6B6B)

val bookColorList = listOf(
    ModernDarkMain to ModernDarkPoint,
    CleanMinimalMain to CleanMinimalPoint,
    CalmNatureMain to CalmNaturePoint,
    MidnightMain to MidnightPoint,
    SoftPastelMain to SoftPastelPoint,
)

val OneDayTextFieldColors
    @Composable
    get() = TextFieldDefaults.colors().copy(
    focusedIndicatorColor = Color.White,
    unfocusedIndicatorColor = Color.White,
    focusedContainerColor = PrimaryColor,
    unfocusedContainerColor = PrimaryColor,
    cursorColor = AccentColor,
    textSelectionColors = TextSelectionColors(
        handleColor = Color.Black, backgroundColor = AccentOpacity40Color
    )
)