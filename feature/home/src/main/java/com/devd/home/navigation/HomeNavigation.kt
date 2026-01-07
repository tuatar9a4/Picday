package com.devd.home.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.home.HomeScreenRoute
import kotlinx.serialization.Serializable


@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onNavigateToEditor: () -> Unit
) {
    composable<HomeRoute> {
        HomeScreenRoute(
            viewModel = hiltViewModel()
        )
    }
}