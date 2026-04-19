package com.devd.onedayoneshot.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.Black33Opacity90Color
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.BlackF4Color
import com.devd.commonsystem.theme.WhiteColor
import com.devd.onedayoneshot.navigation.NaviBarItem

@Preview
@Composable
fun MainNavigationBar(
    navigationItems: List<NaviBarItem> = listOf(
        NaviBarItem.Library,
        NaviBarItem.Home,
        NaviBarItem.Calendar
    ),
    currentScreen: MutableState<NaviBarItem> = remember { mutableStateOf(NaviBarItem.Home) },
    navController: NavHostController = rememberNavController()
) {
    NavigationBar(
        modifier = Modifier
            .padding(0.dp)
            .border(
                width = 1.dp,
                color = BlackF4Color,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            )
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            ),
        containerColor = WhiteColor,
        tonalElevation = 0.dp
    ) {
        navigationItems.forEach {
            NavigationBarItem(
                modifier = Modifier.padding(0.dp),
                icon = {
                    Icon(
                        painter = painterResource(it.icon),
                        contentDescription = null
                    )
                },
                colors = NavigationBarItemDefaults.colors().copy(
                    selectedTextColor = Black33Color,
                    selectedIconColor = Black33Color,
                    selectedIndicatorColor = Black33Opacity90Color,
                    unselectedTextColor = BlackD9Color,
                    unselectedIconColor = BlackD9Color
                ),
                label = { Text(text = it.screenName) },
                selected = currentScreen.value == it,
                onClick = {
                    currentScreen.value = it
                    navController.navigate(it.mainRoute){
                        // 기존 탭의 상태(스크롤 등)를 저장
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        // 2. 중복 방지:
                        // 현재 화면이 이미 선택된 탭이라면 다시 생성하지 않습니다.
                        launchSingleTop = true
                        // 3. 상태 복원:
                        // 이전에 저장된 상태가 있다면(예: Home의 스크롤 위치) 복구합니다.
                        restoreState = true
                    }
                }
            )
        }
    }
}