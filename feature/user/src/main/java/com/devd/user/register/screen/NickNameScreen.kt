package com.devd.user.register.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.theme.TextOpacity80Color
import com.devd.commonsystem.ui.SingleLineTextField
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.utils.checkValidateRex

@Composable
fun NickNameScreen(
    editText: MutableState<String>,
    checkValidateId: (String) -> Boolean = { true },
    onSnackBarMessage: (String) -> Unit,
    onDone: () -> Unit = {}
) {
    val validateId = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = "사용하실 ID를 알려주세요!",
            style = OneDayTypography.titleMedium.copy(
                color = TextDefaultColor
            )
        )
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = "한글,영문,숫자 최대 2~10자/ 공백,특수기호 불가",
            style = OneDayTypography.labelLarge.copy(
                color = TextOpacity80Color
            )
        )
        Spacer(Modifier.height(40.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            SingleLineTextField(
                modifier = Modifier
                    .weight(1f),
                editText = editText,
                onTextChange = { text ->
                    if (!text.checkValidateRex("^[가-힣a-zA-Z0-9]{0,10}$"))
                        return@SingleLineTextField
                    validateId.value = false
                    editText.value = text
                },
                hintText = R.string.nickname_input_hint,
                imeAction = ImeAction.Done,
                onDone = {
                    focusManager.clearFocus()
                    if (validateId.value) {
                        onDone.invoke()
                    } else {
                        onSnackBarMessage.invoke("중복확인을 해주세요")
                    }
                }
            )

            TextButton(
                text = "중복확인",
                enable = !validateId.value,
                onClick = {
                    validateId.value = checkValidateId.invoke(editText.value)
                }
            )
            Spacer(Modifier.width(20.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier
                .padding(horizontal = 20.dp),
            text = "(${editText.value.length}/10)",
            style = OneDayTypography.labelLarge.copy(
                color = TextDefaultColor
            )
        )
    }
}


@Preview
@Composable
fun NickNameScreenPreview() {
    NickNameScreen(
        editText = mutableStateOf(""),
        onSnackBarMessage = {}
    )
}