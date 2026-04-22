package com.devd.onedayoneshot.navigation

import com.devd.bookcase.navigation.BookcaseNaviRoute
import com.devd.calendar.navigation.CustomCalendarRoute
import com.devd.commonsystem.R
import com.devd.home.navigation.HomeRoute
import com.devd.model.local.NavRoute

sealed class NaviBarItem(
    val icon: Int,
    val screenName: String,
    val mainRoute : NavRoute,
    val showBottomBar: Boolean,
) {
    object Library : NaviBarItem(R.drawable.icon_library, "서재", BookcaseNaviRoute, true)
    object Home : NaviBarItem(R.drawable.icon_pencil, "기록",HomeRoute, true)
    object Calendar : NaviBarItem(R.drawable.icon_calendar, "달력", CustomCalendarRoute, true)
}