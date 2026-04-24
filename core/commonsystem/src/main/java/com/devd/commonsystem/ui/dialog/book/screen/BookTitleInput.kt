package com.devd.commonsystem.ui.dialog.book.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.Black88Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.RedColor
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType

@Composable
fun BookTitleInput(
    modifier: Modifier,
    type: DiaryBookDialogType,
    titleTextFieldState: TextFieldState,
    isErrorTitle: MutableState<Boolean>
) {

    Column(
        modifier = modifier,
    ) {
        Text(
            text = if (isErrorTitle.value) stringResource(R.string.error_title_message) else "",
            style = MaterialTheme.typography.labelSmall.copy(
                color = RedColor
            )
        )
        Spacer(Modifier.height(5.dp))
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth(),
            state = titleTextFieldState,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = TextDefaultColor
            ),
            readOnly = type == DiaryBookDialogType.VIEW,
            inputTransformation = {
                if (asCharSequence().length > 10) revertAllChanges()
                isErrorTitle.value = false
            },
            decorator = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (titleTextFieldState.text.isEmpty()) {
                        Text(
                            text = "제목을 입력하세요", // 원하는 힌트 문구
                            style = MaterialTheme.typography.titleMedium,
                            color = Black88Color // 힌트 컬러
                        )
                    }
                    innerTextField() // 실제 입력창 호출
                }
            }
        )
        Spacer(Modifier.height(2.dp))
        if (type == DiaryBookDialogType.EDIT) HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = BlackColor
        )
    }
}