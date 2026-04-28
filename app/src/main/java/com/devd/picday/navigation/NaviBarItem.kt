package com.devd.picday.navigation

import com.devd.bookcase.navigation.BookcaseNaviRoute
import com.devd.calendar.navigation.CustomCalendarRoute
import com.devd.commonsystem.R
import com.devd.home.navigation.HomeRoute
import com.devd.model.local.NavRoute
import com.devd.setting.navigation.SettingNaviRoute

sealed class NaviBarItem(
    val icon: Int,
    val screenName: String,
    val mainRoute : NavRoute,
    val showBottomBar: Boolean,
) {
    object Library : NaviBarItem(R.drawable.icon_library, "서재", BookcaseNaviRoute, true)
    object Home : NaviBarItem(R.drawable.icon_pencil, "기록",HomeRoute, true)
    object Calendar : NaviBarItem(R.drawable.icon_calendar, "달력", CustomCalendarRoute, true)
    object Setting : NaviBarItem(R.drawable.icon_setting, "설정", SettingNaviRoute, true)
}