package com.devd.setting.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devd.model.local.NavRoute
import com.devd.setting.SettingScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data object SettingNaviRoute/*(
    val userUUID: String
)*/ : NavRoute

fun NavGraphBuilder.settingScreen(
    modifier: Modifier = Modifier,
    onLockPage: (Boolean) ->Unit,
    onBackClick: () -> Unit = {}
) {
    composable<SettingNaviRoute> {
        SettingScreenRoute(
            modifier = modifier,
            onBackClick = onBackClick,
            onLockPage = onLockPage
        )
    }
}