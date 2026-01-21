package com.devd.editor.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.editor.EditorScreenRoute
import kotlinx.serialization.Serializable


@Serializable
data object EditorRoute


fun NavGraphBuilder.editorScreen(
    modifier : Modifier = Modifier
) {
    composable<EditorRoute> {
        EditorScreenRoute(
            modifier = modifier
        )
    }
}
