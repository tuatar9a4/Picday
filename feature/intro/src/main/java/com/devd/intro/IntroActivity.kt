package com.devd.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.intro.screen.IntroScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroActivity : ComponentActivity() {

    val viewModel: IntroViewModel by viewModels()

    val registerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OneDayOneShotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    IntroScreen(
                        modifier = Modifier.padding(innerPadding),
                        onMakeDiaryClick = ::moveToRegisterPage,
                        onLoginClick = ::tempCheckSavedID
                    )
                }
            }
        }
    }


    private fun moveToRegisterPage() {
        val intent = Intent()
        intent.setClassName(this, "com.devd.user.register.RegisterActivity")
        registerResult.launch(intent)
    }


    private fun tempCheckSavedID() {
        lifecycleScope.launch {
            if (!viewModel.fetchSavedNickName()) return@launch
            val intent = Intent()
            intent.setClassName(this@IntroActivity, "com.devd.onedayoneshot.MainActivity")
            startActivity(intent)
            finish()
        }
    }


}