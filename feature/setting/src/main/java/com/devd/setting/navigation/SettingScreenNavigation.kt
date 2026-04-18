package com.devd.setting.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.setting.SettingScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data class SettingNaviRoute(
    val userUUID: String
)

fun NavGraphBuilder.settingScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    composable<SettingNaviRoute> {
        SettingScreenRoute(
            modifier = modifier,
            onBackClick = onBackClick
        )
    }
}