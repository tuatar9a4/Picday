package com.devd.commonsystem.ui.dialog.book.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType

@Composable
fun BookDescriptionInput(
    modifier: Modifier,
    type: DiaryBookDialogType,
    descriptionTextFieldState: TextFieldState
) {
    BasicTextField(
        modifier = modifier.then(
            Modifier
                .border(1.dp, BlackD9Color, RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .height(100.dp)
                .padding(vertical = 5.dp, horizontal = 10.dp)

        ),
        state = descriptionTextFieldState,
        readOnly = type == DiaryBookDialogType.VIEW,
        textStyle = MaterialTheme.typography.bodyMedium,
        decorator = { innerTextField ->
            Box(contentAlignment = Alignment.TopStart) {
                if (descriptionTextFieldState.text.isEmpty()) {
                    Text(
                        text = "일기 설명을 입력하세요.", // 원하는 힌트 문구
                        style = MaterialTheme.typography.bodyMedium,
                        color = BlackOpacity40Color // 힌트 컬러
                    )
                }
                innerTextField() // 실제 입력창 호출
            }
        }
    )
}