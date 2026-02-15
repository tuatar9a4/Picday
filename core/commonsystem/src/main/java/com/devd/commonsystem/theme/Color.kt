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


//https://kwongdevelop.tistory.com/5
val PrimaryColor = Color(0xFFF5F1EB)
val SecondaryColor = Color(0xFFC8B6A6)
val TextDefaultColor = Color(0xFF2E2E2E)
val TextOpacity80Color = Color(0xCC2E2E2E)
val AccentColor = Color(0xFFC38EB4)
val AccentOpacity40Color = Color(0x66C38EB4)
val WhiteColor = Color(0xFFFFFFFF)
val WhiteOpacity40Color = Color(0x66FFFFFF)
val BlackColor = Color(0xFF000000)
val BlackOpacity40Color = Color(0x66000000)
val RedColor = Color(0xFFE3242B)
val GreyColor = Color(0xFF8F8A84)
val GreyOpacity40Color = Color(0x668F8A84)




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