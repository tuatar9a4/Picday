package com.devd.user.register.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.theme.TextOpacity80Color
import com.devd.commonsystem.ui.SingleLineTextField

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PasswordScreen(
    passwordText: MutableState<String> = mutableStateOf(""),
    rePasswordText: MutableState<String> = mutableStateOf(""),
    onDone: () -> Unit = {}
) {

    val passwordError = remember { mutableStateOf(false) }
    val rePasswordError = remember { mutableStateOf(false) }

    val rePasswordFocus = remember { FocusRequester() }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = "사용하실 비밀번호를 입력해주세요!",
            style = OneDayTypography.titleMedium.copy(
                color = TextDefaultColor
            )
        )
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = "최소 7자 이상/ 공백 불가",
            style = OneDayTypography.labelLarge.copy(
                color = TextOpacity80Color
            )
        )
        Spacer(Modifier.height(40.dp))
        SingleLineTextField(
            modifier = Modifier
                .fillMaxWidth(),
            editText = passwordText,
            onTextChange = { text ->
                passwordError.value = (passwordText.value.length < 7)
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
                rePasswordError.value = rePasswordText.value != passwordText.value
                rePasswordText.value = text
            },
            isError = rePasswordError.value,
            hintText = R.string.password_repeat_input_hint,
            isPassword = true,
            imeAction = ImeAction.Done,
            onDone = onDone
        )

    }
}