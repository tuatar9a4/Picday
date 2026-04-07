package com.devd.commonsystem.ui.dialog.book.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType

@Composable
fun BookDescriptionInput(
    modifier: Modifier,
    type: DiaryBookDialogType,
    descriptionTextFieldState: TextFieldState
) {
    val contentsEditorBg = if (type == DiaryBookDialogType.VIEW) {
        Modifier
    } else {
        Modifier.border(1.dp, BlackOpacity40Color, RoundedCornerShape(5.dp))
    }
    BasicTextField(
        modifier = modifier.then(
            contentsEditorBg.then(
                Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(5.dp)
            )
        ),
        state = descriptionTextFieldState,
        readOnly = type == DiaryBookDialogType.VIEW,
        textStyle = OneDayTypography.bodyMedium,
        decorator = { innerTextField ->
            Box(contentAlignment = Alignment.TopStart) {
                if (descriptionTextFieldState.text.isEmpty()) {
                    Text(
                        text = "일기 설명을 입력하세요.", // 원하는 힌트 문구
                        style = OneDayTypography.bodyMedium,
                        color = BlackOpacity40Color // 힌트 컬러
                    )
                }
                innerTextField() // 실제 입력창 호출
            }
        }
    )
}