package com.devd.user.register.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.SingleLineTextField
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.user.register.data.MessageType
import com.devd.user.register.login.LoginViewModel

@Composable
fun LoginScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    moveToHome: () -> Unit,
    onBackClick: () -> Unit
) {

    val uiState by viewModel.loginUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.userResult.collect {
            moveToHome()
        }
    }

    LoginScreen(
        modifier = modifier,
        requestLogin = viewModel::requestLogin,
        onBackClick = onBackClick
    )

    uiState.messageInfo?.messageStr?.ShowMessageDialog(
        onRightButtonClick = {
            when (uiState.messageInfo!!.type) {
                MessageType.LOGIN_FAIL -> {
                    viewModel.dismissMessageDialog()
                }
            }
        }
    )
    uiState.isLoading.LoadingDialog()
}


@Preview
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    requestLogin: (id: String, pw: String) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {}
) {

    val idStr = remember { mutableStateOf("") }
    val pwStr = remember { mutableStateOf("") }

    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(color = PrimaryColor)
        )
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 15.dp, start = 15.dp)
                .clickable(onClick = onBackClick),
            painter = painterResource(R.drawable.icon_back_arrow),
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .background(color = PrimaryColor)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "OneDay OneShot",
                style = OneDayTypography.bodyLarge.copy(
                    color = BlackColor
                )
            )
            Spacer(Modifier.height(10.dp))
            SingleLineTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = WhiteColor, shape = RoundedCornerShape(20.dp))
                    .border(1.dp, color = BlackColor, shape = RoundedCornerShape(20.dp)),
                editText = idStr,
                hintText = R.string.id_text,
                hintColor = GreyColor,
                bottomLine = false,
                onTextChange = { text ->
                    idStr.value = text
                }
            )
            Spacer(Modifier.height(5.dp))
            SingleLineTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = WhiteColor, shape = RoundedCornerShape(20.dp))
                    .border(1.dp, color = BlackColor, shape = RoundedCornerShape(20.dp)),
                editText = pwStr,
                hintText = R.string.pw_text,
                isPassword = true,
                hintColor = GreyColor,
                bottomLine = false,
                onTextChange = { text ->
                    pwStr.value = text
                }
            )
            Spacer(Modifier.height(15.dp))
            Button(
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = AccentColor
                ),
                onClick = {
                    requestLogin(idStr.value, pwStr.value)
                    pwStr.value = ""
                }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    text = stringResource(R.string.login_text),
                    style = OneDayTypography.bodyLarge.copy(
                        color = WhiteColor,
                    ),
                    textAlign = TextAlign.Center

                )
            }
        }
    }
}