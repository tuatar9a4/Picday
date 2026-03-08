package com.devd.onedayoneshot.ui

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.devd.calendar.navigation.CustomCalendarRoute
import com.devd.calendar.navigation.customCalendarScreen
import com.devd.commonsystem.utils.LocalSharedTransitionScope
import com.devd.diary.navigation.DiaryListRoute
import com.devd.diary.navigation.diaryListScreen
import com.devd.editor.navigation.EditorRoute
import com.devd.editor.navigation.editorScreen
import com.devd.home.navigation.HomeRoute
import com.devd.home.navigation.homeScreen
import com.devd.model.local.EditMode

@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    //HomeRoute, EditorRoute, DiaryListRoute, CustomCalendarRoute
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
        ) {
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
            ) {
                homeScreen(
                    modifier = modifier,
                    onNavigateToEditor = { uri, bookId, diaryId ->
                        navController.navigate(
                            EditorRoute(
                                currentTime = System.currentTimeMillis(),
                                editMode = EditMode.Edit,
                                imageUrl = uri,
                                bookId = bookId,
                                diaryId = diaryId
                            )
                        )
                    },
                    onNavigateToList = { list, pos ->
                        navController.navigate(
                            DiaryListRoute(
                                initList = list,
                                startPos = pos
                            )
                        )
                    },
                    onNavigateToCalendar = { bookId, selectMillis ->
                        navController.navigate(
                            CustomCalendarRoute(
                                selectBookID = bookId,
                                selectMillis = selectMillis
                            )
                        )
                    }
                )

                editorScreen(
                    modifier = modifier,
                    backListener = { changeId ->
                        changeId?.let {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("ChangeID", changeId)
                        }
                        navController.popBackStack()
                    }
                )

                diaryListScreen(
                    modifier = modifier,
                    onNavigateToEditor = { uri, bookId, diaryId ->
                        navController.navigate(
                            EditorRoute(
                                currentTime = System.currentTimeMillis(),
                                editMode = EditMode.EditOnlyThis,
                                imageUrl = uri,
                                bookId = bookId,
                                diaryId = diaryId
                            )
                        )
                    },
                    backListener = { navController.popBackStack() }
                )

                customCalendarScreen(
                    modifier = modifier,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}