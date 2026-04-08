package com.devd.user.register.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.theme.TextOpacity80Color
import com.devd.commonsystem.ui.SingleLineTextField
import com.devd.commonsystem.utils.StringRexFormat.NICKNAME_REGEX
import com.devd.commonsystem.utils.checkValidateRex


@Preview
@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    nickName: MutableState<String> = mutableStateOf(""),
//    diaryName: MutableState<String> = mutableStateOf(""),
//    onDone: () -> Unit = {}
) {

    val diaryNameFocus = remember { FocusRequester() }

    Column(
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.introduce_ask_text),
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextDefaultColor
            )
        )
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.diary_book_reg_message),
            style = MaterialTheme.typography.labelLarge.copy(
                color = TextOpacity80Color
            )
        )
        Spacer(Modifier.height(5.dp))
        SingleLineTextField(
            modifier = Modifier.fillMaxWidth(),
            editText = nickName,
            onTextChange = { text ->
                if (!text.checkValidateRex(NICKNAME_REGEX)) return@SingleLineTextField
                nickName.value = text
            },
            imeAction = ImeAction.Next,
            onNext = {
                diaryNameFocus.requestFocus()
            }

        )
//        Spacer(Modifier.height(15.dp))
//        Text(
//            modifier = Modifier.padding(horizontal = 20.dp),
//            text = stringResource(R.string.diary_book_make_message),
//            style = MaterialTheme.typography.titleMedium.copy(
//                color = TextDefaultColor
//            )
//        )
//        Spacer(Modifier.height(10.dp))
//        SingleLineTextField(
//            modifier = Modifier.fillMaxWidth(),
//            editText = diaryName,
//            onTextChange = { text ->
//                diaryName.value = text
//            },
//            imeAction = ImeAction.Done,
//            onDone = onDone
//        )
    }

}