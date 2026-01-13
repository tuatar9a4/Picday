package com.devd.home.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.home.screen.HomeScreenRoute
import kotlinx.serialization.Serializable


@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    modifier : Modifier = Modifier,
    onNavigateToEditor: () -> Unit
) {
    composable<HomeRoute> {
        HomeScreenRoute(
//            viewModel = hiltViewModel()
            modifier = modifier,
            onEditorClick = onNavigateToEditor
        )
    }
}