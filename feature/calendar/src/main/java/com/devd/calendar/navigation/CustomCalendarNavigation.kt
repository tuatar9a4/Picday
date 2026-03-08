package com.devd.calendar.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.calendar.CalendarScreenRoute
import kotlinx.serialization.Serializable


@Serializable
data class CustomCalendarRoute(
    val selectBookID: Long,
    val selectMillis: Long
)

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