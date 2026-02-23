package com.devd.user.register.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.ui.Toolbar
import com.devd.user.register.RegisterViewModel
import com.devd.user.register.data.RegisterStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterRoute(
    modifier: Modifier = Modifier,
    viewmodel: RegisterViewModel,
    onSnackBar: (String) -> Unit = {},
    backClick: () -> Unit = {}
) {
    val currentStep = remember { mutableStateOf(RegisterStep.Step3) }
    val changeStep = {
        when (currentStep.value) {
            RegisterStep.Step1 -> {
                if(!viewmodel.isCheckDuplicate.value){
                    onSnackBar.invoke("중복 확인을 해주세요")
                }else{
                    currentStep.value = RegisterStep.Step2
                }
            }
            RegisterStep.Step2 -> currentStep.value = RegisterStep.Step3
            RegisterStep.Step3 -> viewmodel.requestMakeId()
        }
    }
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
            leftButtonClick = {
                when (currentStep.value) {
                    RegisterStep.Step1 -> backClick.invoke()
                    RegisterStep.Step2 -> currentStep.value = RegisterStep.Step1
                    RegisterStep.Step3 -> currentStep.value = RegisterStep.Step2

                }
            }
        )
        Spacer(Modifier.height(60.dp))
        when (currentStep.value) {
            RegisterStep.Step1 -> NickNameScreen(
                modifier = Modifier.weight(1f),
                editText = viewmodel.id,
                isCheckDuplicate = viewmodel.isCheckDuplicate,
                checkValidateId = viewmodel::checkValidateId,
                onDone = changeStep
            )

            RegisterStep.Step2 -> PasswordScreen(
                modifier = Modifier.weight(1f),
                passwordText = viewmodel.password,
                onSnackBarMessage = onSnackBar,
                onDone = changeStep
            )

            RegisterStep.Step3 -> InfoScreen(
                modifier = Modifier.weight(1f),
                nickName = viewmodel.nickname,
//                diaryName = viewmodel.diaryName,
//                onDone = changeStep
            )
        }
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            text = "Next",
            textSize = 20.sp,
            contentsPadding = PaddingValues(vertical = 15.dp, horizontal = 20.dp),
            onClick = changeStep
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Preview
@Composable
fun NickNamePreview() {
    NickNameScreen(
        editText = mutableStateOf(""),
        isCheckDuplicate = mutableStateOf(false),
    )
}

@Preview
@Composable
fun PasswordPreview() {
    PasswordScreen(
        passwordText = mutableStateOf(""),
        onSnackBarMessage = {}
    )
}
@Preview
@Composable
fun InfoPreview() {
    InfoScreen (

    )
}