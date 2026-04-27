package com.devd.user.register

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
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
        collectFlow()
        setContent {
            OneDayOneShotTheme {
                val snackBarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                val callSnack: (String) -> Unit = {
                    coroutineScope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
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

    fun collectFlow() {
        lifecycleScope.launch {
            regiViewModel.uiState.collect {
                when (it) {
                    is RegisterUIState.SuccessMakeId -> {
                        val intent = Intent()
                        intent.setClassName(
                            this@RegisterActivity, "com.devd.picday.MainActivity"
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

}