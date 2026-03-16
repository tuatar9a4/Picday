package com.devd.editor.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.editor.screen.EditorScreenRoute
import com.devd.model.local.EditMode
import kotlinx.serialization.Serializable


@Serializable
data class EditorRoute(
    val editMode: EditMode,
    val currentTime: Long,
    val imageUrl: String?,
    val bookId: Long,
    val diaryId: Long?
)


fun NavGraphBuilder.editorScreen(
    modifier: Modifier = Modifier,
    backListener: (Long?) -> Unit
) {
    composable<EditorRoute> { backstackEntry ->
        EditorScreenRoute(
            modifier = modifier,
            onBackIconClick = backListener,
        )
    }
}
