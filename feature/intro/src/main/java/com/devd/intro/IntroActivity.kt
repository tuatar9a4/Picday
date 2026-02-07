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
import com.devd.intro.data.Loading
import com.devd.intro.data.MoveToHome
import com.devd.intro.screen.IntroScreenRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class IntroActivity : ComponentActivity() {

    val viewModel: IntroViewModel by viewModels()

    val registerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                RESULT_OK -> finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        collectData()
        setContent {
            OneDayOneShotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    IntroScreenRoute(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel,
                        onMakeDiaryClick = ::moveToRegisterPage,
                        onLoginClick = ::moveToHomePage
                    )
                }
            }
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            viewModel.introUiState.collect {
                Timber.d("Check -> $it")
                when (it) {
                    is Loading -> viewModel.changeLoadingState(it.isShow)
                    MoveToHome -> moveToHomePage()
                }
            }
        }
    }

    private fun moveToRegisterPage() {
        val intent = Intent()
        intent.setClassName(this, "com.devd.user.register.RegisterActivity")
        registerResult.launch(intent)
    }


    private fun moveToHomePage() {
        lifecycleScope.launch {
            if (!viewModel.fetchSavedNickName()) return@launch
            val intent = Intent()
            intent.setClassName(this@IntroActivity, "com.devd.onedayoneshot.MainActivity")
            startActivity(intent)
            finish()
        }
    }


}