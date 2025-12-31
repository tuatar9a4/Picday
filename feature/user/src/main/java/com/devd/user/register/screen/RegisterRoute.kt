package com.devd.user.register.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.user.register.RegisterViewModel
import com.devd.user.register.data.RegisterStep
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterRoute(
    modifier: Modifier = Modifier,
    viewmodel: RegisterViewModel,
    onSnackBar: (String) -> Unit = {},
    backClick: () -> Unit = {}
) {
    val currentStep = remember { mutableStateOf(RegisterStep.Step1) }
    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(
                    color = PrimaryColor
                )
        )
    ) {
        Toolbar(
            title = "Register",
            leftButtonClick = backClick
        )
        Spacer(Modifier.height(60.dp))
        when (currentStep.value) {
            RegisterStep.Step1 -> NickNameScreen(
                editText = viewmodel.nickName,
                onSnackBarMessage = onSnackBar,
                checkValidateId = viewmodel::checkValidateId,
                onDone = {
                    Timber.d("끝나 부렀으~")
                }
            )

            RegisterStep.Step2 -> NickNameScreen(
                editText = viewmodel.nickName,
                onSnackBarMessage = onSnackBar,
            )

            RegisterStep.Step3 -> NickNameScreen(
                editText = viewmodel.nickName,
                onSnackBarMessage = onSnackBar,
            )
        }
    }
}

@Preview
@Composable
fun RegisterRoutePreview() {
    NickNameScreen(
        editText = mutableStateOf(""),
        onSnackBarMessage = {}
    )
}