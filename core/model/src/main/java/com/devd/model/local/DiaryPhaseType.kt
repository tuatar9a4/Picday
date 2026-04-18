package com.devd.model.local

import androidx.annotation.DrawableRes
import com.devd.model.R

enum class DiaryPhaseType(@param:DrawableRes vararg val ids: Int) {
    MOON(
        R.drawable.moon_phase_06,
        R.drawable.moon_phase_05,
        R.drawable.moon_phase_04,
        R.drawable.moon_phase_03,
        R.drawable.moon_phase_02,
        R.drawable.moon_phase_01,
        R.drawable.moon_phase_00
    )

}
