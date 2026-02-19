package com.devd.onedayoneshot.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.devd.editor.navigation.EditorRoute
import com.devd.editor.navigation.editorScreen
import com.devd.home.navigation.HomeRoute
import com.devd.home.navigation.homeScreen
import timber.log.Timber


@Composable
fun MyNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    //HomeRoute, EditorRoute
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        homeScreen(
            modifier = modifier,
            onNavigateToEditor = { uri, bookId, diaryId ->
                Timber.d("???Move...???? uri :$uri")
                navController.navigate(
                    EditorRoute(
                        currentTime = System.currentTimeMillis(),
                        imageUrl = uri,
                        bookId = bookId,
                        diaryId = diaryId
                    )
                )
            }
        )

        editorScreen(
            modifier = modifier,
            backListener = {navController.popBackStack()}
        )
    }

}


//@Composable
//fun AppNavHost(
//    navController: NavHostController = rememberNavController()
//) {
//    NavHost(
//        navController = navController,
//        startDestination = HomeRoute,
//        modifier = Modifier
//    ) {
//        homeScreen {
//
//        }
//
//        detailNavGraph(
//            onBack = { navController.popBackStack() }
//        )
//    }
//}