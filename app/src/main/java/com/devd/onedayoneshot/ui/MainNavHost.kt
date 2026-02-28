package com.devd.onedayoneshot.ui

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.devd.commonsystem.utils.LocalSharedTransitionScope
import com.devd.diary.navigation.DiaryListRoute
import com.devd.diary.navigation.diaryListScreen
import com.devd.editor.navigation.EditorRoute
import com.devd.editor.navigation.editorScreen
import com.devd.home.navigation.HomeRoute
import com.devd.home.navigation.homeScreen

@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    //HomeRoute, EditorRoute, DiaryListRoute
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
                    backListener = { navController.popBackStack() }
                )

                diaryListScreen(
                    modifier = modifier,
                    backListener = { navController.popBackStack() }
                )
            }
        }
    }
}