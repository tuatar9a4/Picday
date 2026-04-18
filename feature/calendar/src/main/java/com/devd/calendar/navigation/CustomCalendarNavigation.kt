package com.devd.calendar.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.calendar.CalendarScreenRoute
import com.devd.model.local.NavRoute
import kotlinx.serialization.Serializable


@Serializable
data object CustomCalendarRoute/*(
    val selectBookID: Long,
    val selectMillis: Long
)*/ : NavRoute

fun NavGraphBuilder.customCalendarScreen(
    modifier: Modifier = Modifier,
    onBackClick : () -> Unit = {}
) {
    composable<CustomCalendarRoute> {
        CalendarScreenRoute(
            modifier = modifier,
            onBackClick = onBackClick
        )
    }
}