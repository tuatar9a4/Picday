package com.devd.commonsystem.utils

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.devd.commonsystem.R

enum class FontList(val fontFamily: FontFamily) {
    S_CORE_DREAM(
        fontFamily = FontFamily(
            Font(R.font.sc_dream_extra_bold, weight = FontWeight(700)),
            Font(R.font.sc_dream_bold, weight = FontWeight(600)),
            Font(R.font.sc_dream_regular, weight = FontWeight(500)),
            Font(R.font.sc_dream_light, weight = FontWeight(400)),
        )
    ),
    STAR_DUST(
        fontFamily = FontFamily(
            Font(R.font.star_dust_extra_bold, weight = FontWeight(700)),
            Font(R.font.star_dust_bold, weight = FontWeight(600)),
            Font(R.font.star_dust_regular, weight = FontWeight(500)),
            Font(R.font.star_dust_regular, weight = FontWeight(400)),
        )
    ),
    MARUBURI(
        fontFamily = FontFamily(
            Font(R.font.maruburi_extra_bold, weight = FontWeight(700)),
            Font(R.font.maruburi_bold, weight = FontWeight(600)),
            Font(R.font.maruburi_regular, weight = FontWeight(500)),
            Font(R.font.maruburi_light, weight = FontWeight(400)),
        )
    ),
    HUMAN_BEOMSEOK(
        fontFamily = FontFamily(
            Font(R.font.human_beomseok_extra_bold, weight = FontWeight(700)),
            Font(R.font.human_beomseok_bold, weight = FontWeight(600)),
            Font(R.font.human_beomseok_regular, weight = FontWeight(500)),
            Font(R.font.human_beomseok_light, weight = FontWeight(400)),
        )
    ),
}