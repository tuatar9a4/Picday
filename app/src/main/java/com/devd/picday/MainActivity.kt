package com.devd.picday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.devd.bookcase.navigation.BookcaseNaviRoute
import com.devd.calendar.navigation.CustomCalendarRoute
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.commonsystem.theme.WhiteColor
import com.devd.home.navigation.HomeRoute
import com.devd.picday.navigation.NaviBarItem
import com.devd.picday.ui.MainNavigationBar
import com.devd.picday.ui.MyNavHost
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
                val items = listOf(
                    NaviBarItem.Library, NaviBarItem.Home, NaviBarItem.Calendar, NaviBarItem.Setting
                )
                val currentScreen = remember { mutableStateOf<NaviBarItem>(NaviBarItem.Home) }
                val isShowBottomNav = remember { mutableStateOf(true) }
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                LaunchedEffect(navBackStackEntry?.destination?.route) {
                    val currentRoute = navBackStackEntry?.destination?.route
                    when (currentRoute) {
                        HomeRoute.javaClass.name -> {
                            isShowBottomNav.value = true
                            currentScreen.value = NaviBarItem.Home
                        }

                        BookcaseNaviRoute.javaClass.name -> {
                            isShowBottomNav.value = true
                            currentScreen.value = NaviBarItem.Library
                        }

                        CustomCalendarRoute.javaClass.name -> {
                            isShowBottomNav.value = true
                            currentScreen.value = NaviBarItem.Calendar
                        }

                        else -> {
                            isShowBottomNav.value = false
                        }
                    }
                }
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
                    ) { isShow ->
                    }
                }
            }
        }
    }


}