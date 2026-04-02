package com.devd.home.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.commonsystem.utils.LocalAnimatedVisibilityScope
import com.devd.home.screen.HomeScreenRoute
import com.devd.model.local.DiaryInfo
import kotlinx.serialization.Serializable


@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    modifier: Modifier = Modifier,
    onNavigateToList: (list: List<DiaryInfo>, pos: Int) -> Unit,
    onNavigateToEditor: (String?, Long, Long?) -> Unit,
    onBookcaseMove: (userId: String) -> Unit,
    onSettingClick :(userId: String) -> Unit,
    onNavigateToCalendar: (bookId: Long, selectMillis: Long) -> Unit
) {
    composable<HomeRoute> {
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this
        ) {
            HomeScreenRoute(
                modifier = modifier,
                onEditorMove = onNavigateToEditor,
                onCalendarMove = onNavigateToCalendar,
                onBookcaseMove = onBookcaseMove,
                onSettingClick = onSettingClick,
                onMoveDiaryList = onNavigateToList
            )
        }
    }
}