package com.devd.picday.ui

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.devd.bookcase.navigation.bookcaseScreen
import com.devd.calendar.navigation.customCalendarScreen
import com.devd.commonsystem.utils.LocalSharedTransitionScope
import com.devd.diary.navigation.DiaryListRoute
import com.devd.diary.navigation.diaryListScreen
import com.devd.editor.navigation.EditorRoute
import com.devd.editor.navigation.editorScreen
import com.devd.home.navigation.HomeRoute
import com.devd.home.navigation.homeScreen
import com.devd.model.local.EditMode
import com.devd.setting.navigation.settingScreen

@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    changeShowBottomBar: (Boolean) -> Unit
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
                    modifier = modifier
                )

                bookcaseScreen(
                    modifier = modifier,
                    onNaviToEditor = { bookId, uri, diaryId ->
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
                    onBackClick = { navController.popBackStack() }
                )
                settingScreen(
                    modifier = modifier,
                    onBackClick = {
                        changeShowBottomBar(true)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}