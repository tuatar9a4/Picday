package com.devd.editor.navigation

import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.devd.editor.EditorScreenRoute
import kotlinx.serialization.Serializable


@Serializable
data class EditorRoute(
    val currentTime: Long,
    val imageUrl: String
)


fun NavGraphBuilder.editorScreen(
    modifier: Modifier = Modifier
) {
    composable<EditorRoute> { backstackEntry ->
        val route = backstackEntry.toRoute<EditorRoute>()

        EditorScreenRoute(
            modifier = modifier,
            currentTime = route.currentTime,
            diaryImage = route.imageUrl.toUri()
        )
    }
}
