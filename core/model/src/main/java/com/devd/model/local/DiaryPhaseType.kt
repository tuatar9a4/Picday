package com.devd.model.local

import androidx.compose.ui.graphics.Color
import com.devd.model.R

enum class DiaryPhaseType(
    val bg: Int,
    val mainColor: Color,
    val subColor: Color,
    val ids: IntArray,
    val names: List<String>,
    val description: List<String>
) {
    MOON(
        bg = R.drawable.img_theme_moon_bg,
        mainColor = Color(0xff7B6FAD),
        subColor = Color(0xffEAE6F4),
        ids = intArrayOf(
            R.drawable.img_theme_moon_01,
            R.drawable.img_theme_moon_02,
            R.drawable.img_theme_moon_03,
            R.drawable.img_theme_moon_04,
            R.drawable.img_theme_moon_05
        ),
        names = listOf(
            "신월",
            "초승",
            "반달",
            "상현",
            "보름"
        ),
        description = listOf(
            "달이 보이지 않아요..",
            "달이 나타났어요!",
            "달이 절반 차올랐어요!",
            "달이 거의 다 찼어요",
            "보름달 완성이에요!",
        )
    ),
    DRAGON(
        bg = R.drawable.img_theme_dragon_bg,
        mainColor = Color(0xff7B6FAD),
        subColor = Color(0xffE6F4F3),
        ids = intArrayOf(
            R.drawable.img_theme_dragon_01,
            R.drawable.img_theme_dragon_02,
            R.drawable.img_theme_dragon_03,
            R.drawable.img_theme_dragon_04,
            R.drawable.img_theme_dragon_05
        ),
        names = listOf(
            "에그",
            "베비용",
            "나래용",
            "대장용",
            "원로용"
        ),
        description = listOf(
            "알입니다.",
            "애기용이 나왔어요",
            "날 준비를 하나봐요",
            "용이 날고 있어요",
            "용이 다 컸네요!",
        )
    ),
    CHICK(
        bg = R.drawable.img_theme_chick_bg,
        mainColor = Color(0xff7B6FAD),
        subColor = Color(0xffF4E6E6),
        ids = intArrayOf(
            R.drawable.img_theme_chick_01,
            R.drawable.img_theme_chick_02,
            R.drawable.img_theme_chick_03,
            R.drawable.img_theme_chick_04,
            R.drawable.img_theme_chick_05
        ),
        names = listOf(
            "에그",
            "병알",
            "병아리",
            "닭아리",
            "닭"
        ),
        description = listOf(
            "뭔가 맛있어 보이는 알이네요",
            "병아리가 나오려 해요!",
            "병아리가 나왔어요!",
            "닭벼슬이 자라고 있어요!",
            "누가봐도 닭이에요! 닭! 누가봐도!",
        )
    ),
    PLANT(
        bg = R.drawable.img_theme_plant_bg,
        mainColor = Color(0xff7B6FAD),
        subColor = Color(0xffE9F4E6),
        ids = intArrayOf(
            R.drawable.img_theme_plant_01,
            R.drawable.img_theme_plant_02,
            R.drawable.img_theme_plant_03,
            R.drawable.img_theme_plant_04,
            R.drawable.img_theme_plant_05
        ),
        names = listOf(
            "씨앗",
            "새싹",
            "올드싹",
            "봉오리",
            "꽃"
        ),
        description = listOf(
            "씨앗이에요",
            "새싹이 올라왔어요",
            "싹이 잘 자라고 있네요",
            "봉오리가 나왔어요!",
            "꽃이 활짝 피었네요!",
        )
    )
}
