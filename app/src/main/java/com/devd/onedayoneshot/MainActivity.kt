package com.devd.onedayoneshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.commonsystem.theme.WhiteColor
import com.devd.onedayoneshot.navigation.NaviBarItem
import com.devd.onedayoneshot.ui.MainNavigationBar
import com.devd.onedayoneshot.ui.MyNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OneDayOneShotTheme(
                changeFontInt = viewModel.appFontCurrent.value
            ) {
                val items = listOf(NaviBarItem.Library, NaviBarItem.Home, NaviBarItem.Calendar)
                val currentScreen = remember { mutableStateOf<NaviBarItem>(NaviBarItem.Home) }
                val isShowBottomNav = remember { mutableStateOf(true) }
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (isShowBottomNav.value) {
                            MainNavigationBar(
                                navigationItems = items,
                                currentScreen = currentScreen,
                                navController = navController
                            )
                        }
                    }
                ) { innerPadding ->
                    MyNavHost(
                        modifier = Modifier
                            .background(WhiteColor)
                            .padding(innerPadding),
                        navController = navController
                    ){ isShow ->
                        isShowBottomNav.value = isShow
                    }
                }
            }
        }
    }


}