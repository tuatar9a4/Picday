package com.devd.editor.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.devd.editor.screen.EditorScreenRoute
import kotlinx.serialization.Serializable


@Serializable
data class EditorRoute(
    val currentTime: Long,
    val imageUrl: String?,
    val bookId: Long,
    val diaryId: Long?
)


fun NavGraphBuilder.editorScreen(
    modifier: Modifier = Modifier,
    backListener : ()->Unit
) {
    composable<EditorRoute> { backstackEntry ->
        val route = backstackEntry.toRoute<EditorRoute>()

        EditorScreenRoute(
            modifier = modifier,
            onBackIconClick = backListener,
            currentTime = route.currentTime,
            diaryImage = route.imageUrl,
            bookId = route.bookId,
            diaryId = route.diaryId
        )
    }
}
