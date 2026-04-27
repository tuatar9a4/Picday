package com.devd.user.register.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.user.register.login.screen.LoginScreenRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OneDayOneShotTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreenRoute(
                        modifier = Modifier.padding(innerPadding),
                        moveToHome = ::moveToHome,
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }

    private fun moveToHome() {
        val intent = Intent()
        intent.setClassName(this, "com.devd.picday.MainActivity")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}