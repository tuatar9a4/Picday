package com.devd.bookcase.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.bookcase.BookcaseRoute
import kotlinx.serialization.Serializable

@Serializable
data class BookcaseNaviRoute(
    val userUUID: String
)

fun NavGraphBuilder.bookcaseScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    composable<BookcaseNaviRoute> {
        BookcaseRoute(
            modifier = modifier,
            onBackPress = onBackClick
        )
    }
}