package com.devd.user.register

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.user.register.screen.RegisterRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : ComponentActivity() {

    private val regiViewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OneDayOneShotTheme {
                val snackBarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                val callSnack: (String) -> Unit = {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(it)
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
                ) { innerPadding ->
                    RegisterRoute(
                        modifier = Modifier.padding(innerPadding),
                        viewmodel = regiViewModel,
                        onSnackBar = callSnack,
                        backClick = { finish() }
                    )
                }
            }
        }

    }

}