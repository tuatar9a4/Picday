package com.devd.commonsystem.ui.dialog.book.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.RedColor
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType
import com.devd.commonsystem.utils.bottomBorder

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
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (type == DiaryBookDialogType.VIEW) Modifier
                    else Modifier.bottomBorder(1.dp, BlackOpacity40Color)
                ),
            state = titleTextFieldState,
            textStyle = OneDayTypography.titleMedium.copy(
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
                            style = OneDayTypography.titleMedium,
                            color = BlackOpacity40Color // 힌트 컬러
                        )
                    }
                    innerTextField() // 실제 입력창 호출
                }
            }
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = if (isErrorTitle.value) stringResource(R.string.error_title_message) else "",
            style = OneDayTypography.labelLarge.copy(
                color = RedColor
            )
        )
    }
}