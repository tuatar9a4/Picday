package com.devd.model.local

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.devd.model.R

enum class DiaryPhaseType(val bg: Int, val color: Color, @param:DrawableRes vararg val ids: Int) {
    MOON(
        bg = R.drawable.img_theme_moon_bg,
        color = Color(0xffEAE6F4),
        ids = intArrayOf(
            R.drawable.img_theme_moon_01,
            R.drawable.img_theme_moon_02,
            R.drawable.img_theme_moon_03,
            R.drawable.img_theme_moon_04,
            R.drawable.img_theme_moon_05
        )
    ),
    DRAGON(
        bg = R.drawable.img_theme_dragon_bg,
        color = Color(0xffE6F4F3),
        ids = intArrayOf(
            R.drawable.img_theme_dragon_01,
            R.drawable.img_theme_dragon_02,
            R.drawable.img_theme_dragon_03,
            R.drawable.img_theme_dragon_04,
            R.drawable.img_theme_dragon_05
        )

    ),
    CHICK(
        bg = R.drawable.img_theme_chick_bg,
        color = Color(0xffF4E6E6),
        ids = intArrayOf(
            R.drawable.img_theme_chick_01,
            R.drawable.img_theme_chick_02,
            R.drawable.img_theme_chick_03,
            R.drawable.img_theme_chick_04,
            R.drawable.img_theme_chick_05
        )

    ),
    PLANT(
        bg = R.drawable.img_theme_plant_bg,
        color = Color(0xffE9F4E6),
        ids = intArrayOf(
            R.drawable.img_theme_plant_01,
            R.drawable.img_theme_plant_02,
            R.drawable.img_theme_plant_03,
            R.drawable.img_theme_plant_04,
            R.drawable.img_theme_plant_05
        )
    )
}
