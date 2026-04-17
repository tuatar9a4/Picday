package com.devd.commonsystem.utils

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.devd.commonsystem.R

enum class FontList(val fontFamily: FontFamily) {
    SUIT(
        fontFamily = FontFamily(
            Font(R.font.suit_extra_bold, weight = FontWeight(700)),
            Font(R.font.suit_semi_bold, weight = FontWeight(600)),
            Font(R.font.suit_medium, weight = FontWeight(500)),
            Font(R.font.suit_light, weight = FontWeight(400)),
        )
    ),
    PINK_PONG(
        fontFamily = FontFamily(
            Font(R.font.pinkfong_font_bold, weight = FontWeight(700)),
            Font(R.font.pinkfong_font_bold, weight = FontWeight(600)),
            Font(R.font.pinkfong_font_regular, weight = FontWeight(500)),
            Font(R.font.pinkfong_font_light, weight = FontWeight(400)),
        )
    ),
    NANUM_SQUARE(
        fontFamily = FontFamily(
            Font(R.font.nanumsquare_extra_bold, weight = FontWeight(700)),
            Font(R.font.nanumsquare_bold, weight = FontWeight(600)),
            Font(R.font.nanumsquare_regular, weight = FontWeight(500)),
            Font(R.font.nanumsquare_light, weight = FontWeight(400)),
        )
    ),
    INTERROP(
        fontFamily = FontFamily(
            Font(R.font.interop_extra_bold, weight = FontWeight(700)),
            Font(R.font.interop_bold, weight = FontWeight(600)),
            Font(R.font.interop_regular, weight = FontWeight(500)),
            Font(R.font.interop_light, weight = FontWeight(400)),
        )
    ),
}