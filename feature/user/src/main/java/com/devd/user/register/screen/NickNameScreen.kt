package com.devd.user.register.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.RedColor
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.theme.TextOpacity80Color
import com.devd.commonsystem.ui.SingleLineTextField
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.utils.StringRexFormat.ID_REGEX
import com.devd.commonsystem.utils.StringRexFormat.ID_WORD_REGEX
import com.devd.commonsystem.utils.checkValidateRex
import kotlinx.coroutines.launch

@Composable
fun NickNameScreen(
    modifier: Modifier = Modifier,
    editText: MutableState<String>,
    isCheckDuplicate: MutableState<Boolean>,
    checkValidateId: suspend (String) -> Unit = { },
    onDone: () -> Unit = {}
) {
    val isValidateId = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = "사용하실 ID를 알려주세요!",
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextDefaultColor
            )
        )
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = "한글,영문,숫자 최대 2~10자/ 공백,특수기호 불가",
            style = MaterialTheme.typography.labelLarge.copy(
                color = TextOpacity80Color
            )
        )
        Spacer(Modifier.height(40.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                SingleLineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    editText = editText,
                    onTextChange = { text ->
                        if ((!text.checkValidateRex(ID_WORD_REGEX) && text.isEmpty()) || text.length > 10) return@SingleLineTextField
                        isCheckDuplicate.value = false
                        editText.value = text
                    },
                    hintText = R.string.nickname_input_hint,
                    imeAction = ImeAction.Done,
                    onDone = {
                        focusManager.clearFocus()
                        onDone.invoke()
                    }
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 20.dp),
                    text = "(${editText.value.length}/10)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = TextDefaultColor
                    )
                )
            }

            TextButton(
                text = "중복확인",
                enable = !isCheckDuplicate.value,
                contentsPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                onClick = {
                    if (editText.value.checkValidateRex(ID_REGEX)) {
                        isValidateId.value = false
                        scope.launch { checkValidateId.invoke(editText.value) }
                    } else {
                        isValidateId.value = true
                    }
                }
            )
            Spacer(Modifier.width(20.dp))
        }
        Spacer(Modifier.height(5.dp))
        if (isValidateId.value) {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = "한글,영문,숫자 최대 2~10자/ 공백,특수기호 불가",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = RedColor
                )
            )
        }
    }
}


@Preview
@Composable
fun NickNameScreenPreview() {
    NickNameScreen(
        editText = remember { mutableStateOf("") },
        isCheckDuplicate = remember { mutableStateOf(false) },
    )
}