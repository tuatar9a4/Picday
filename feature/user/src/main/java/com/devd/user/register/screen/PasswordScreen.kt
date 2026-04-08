package com.devd.user.register.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.theme.TextOpacity80Color
import com.devd.commonsystem.ui.SingleLineTextField
import com.devd.commonsystem.utils.StringRexFormat.PASSWORD_REGEX
import com.devd.commonsystem.utils.checkValidateRex

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PasswordScreen(
    modifier: Modifier = Modifier,
    passwordText: MutableState<String> = mutableStateOf(""),
    onSnackBarMessage: (String) -> Unit = {},
    onDone: () -> Unit = {}
) {

    val rePasswordText = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf(false) }
    val rePasswordError = remember { mutableStateOf(false) }
    val rePasswordFocus = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

    Column(

        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = "사용하실 비밀번호를 입력해주세요!",
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextDefaultColor
            )
        )
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = "영문 및 숫자 1자리 포함 8자 이상 20자 미만",
            style = MaterialTheme.typography.labelLarge.copy(
                color = TextOpacity80Color
            )
        )
        Spacer(Modifier.height(40.dp))
        SingleLineTextField(
            modifier = Modifier
                .fillMaxWidth(),
            editText = passwordText,
            onTextChange = { text ->
                passwordError.value = !text.checkValidateRex(PASSWORD_REGEX)
                rePasswordError.value = rePasswordText.value != text
                passwordText.value = text
            },
            isError = passwordError.value,
            hintText = R.string.password_input_hint,
            isPassword = true,
            imeAction = ImeAction.Next,
            onNext = {
                rePasswordFocus.requestFocus()
            }
        )
        Spacer(Modifier.height(10.dp))
        SingleLineTextField(
            modifier = Modifier
                .focusRequester(rePasswordFocus)
                .fillMaxWidth(),
            editText = rePasswordText,
            onTextChange = { text ->
                rePasswordError.value = text != passwordText.value
                rePasswordText.value = text
            },
            isError = rePasswordError.value,
            hintText = R.string.password_repeat_input_hint,
            isPassword = true,
            imeAction = ImeAction.Done,
            onDone = {
                if (passwordText.value.checkValidateRex(PASSWORD_REGEX) && rePasswordText.value == passwordText.value) {
                    onDone.invoke()
                } else {
                    focusManager.clearFocus()
                    onSnackBarMessage.invoke("비밀번호를 확인 해주세요")
                }
            }
        )

    }
}