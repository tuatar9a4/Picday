package com.devd.bookcase.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.bookcase.BookcaseRoute
import kotlinx.serialization.Serializable

@Serializable
data object BookcaseNaviRoute

fun NavGraphBuilder.bookcaseScreen(
    modifier: Modifier = Modifier,
    onNaviToEditor: (bookId: Long,url: String?, diaryId: Long?) -> Unit,
    onBackClick: () -> Unit = {}
) {
    composable<BookcaseNaviRoute> {
        BookcaseRoute(
            modifier = modifier,
            onNaviToEditor = onNaviToEditor,
            onBackPress = onBackClick
        )
    }
}