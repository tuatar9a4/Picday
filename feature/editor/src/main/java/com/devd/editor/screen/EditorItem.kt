package com.devd.editor.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.Black88Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.OneDayTextFieldColors
import com.devd.commonsystem.theme.SemiVioletColor
import com.devd.commonsystem.theme.WhiteColor

@Preview
@Composable
fun EditorITmeScreen() {
    EditorItem(
        textFieldState = rememberTextFieldState(),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorItem(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    onChangeDiaryText: (String) -> Unit = {},
    onChangeTagItem: (String?, String?) -> Unit = { _, _ -> },
    hashList: List<String> = emptyList()
) {
    val hashTextField = rememberTextFieldState()

    fun addHashTag(hashTag: String) {
        if (hashList.contains(hashTag)) return
        onChangeTagItem.invoke(hashTag, null)
    }

    fun removeHashTag(hashTag: String) {
        if (!hashList.contains(hashTag)) return
        onChangeTagItem.invoke(null, hashTag)

    }

    Column(
        modifier = modifier.then(Modifier)
    ) {
        Column(
            modifier = Modifier
                .background(color = WhiteColor, shape = RoundedCornerShape(15.dp))
                .border(width = 1.dp, color = BlackD9Color, shape = RoundedCornerShape(15.dp))
                .padding(horizontal = 15.dp, vertical = 10.dp),
        ) {
            Text(
                text = "기록하기",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Black33Color
                )
            )
            Spacer(Modifier.height(5.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = with(LocalDensity.current) { (30.sp * 2).toDp() }),
                state = textFieldState,
                lineLimits = TextFieldLineLimits.MultiLine(1, 2),
                placeholder = {
                    Text(
                        modifier = Modifier.align(Alignment.Start),
                        text = "오늘의 일기를 기록하세요 (최대 50자)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Black88Color,
                        )
                    )
                },
                inputTransformation = {
                    if (length > 50) revertAllChanges()
                    onChangeDiaryText.invoke(asCharSequence().toString())
                },
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(0.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = BlackColor
                ),
                colors = OneDayTextFieldColors
            )   // 일기 내용 입력창
        }
        Spacer(Modifier.height(5.dp))
        Text(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 15.dp),
            text = "${textFieldState.text.length}/50",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Black88Color
            )
        )
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .background(color = WhiteColor, shape = RoundedCornerShape(15.dp))
                .border(width = 1.dp, color = BlackD9Color, shape = RoundedCornerShape(15.dp))
                .padding(horizontal = 15.dp, vertical = 10.dp),
        ) {
            Text(
                text = "해시태그",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Black33Color
                )
            )
            Spacer(Modifier.height(5.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 15.dp),
                state = hashTextField,
                placeholder = {
                    Text(
                        modifier = Modifier.align(Alignment.Start),
                        text = "띄어쓰기를 사용하면 해시태그가 적용 됩니다",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Black88Color,
                        )
                    )
                },
                lineLimits = TextFieldLineLimits.MultiLine(1, 1),
                inputTransformation = {
                    if (length > 8) revertAllChanges()
                    if (asCharSequence().toString().contains(" ")) {
                        if (asCharSequence().isNotBlank()) addHashTag(hashTextField.text.toString())
                        delete(0, length)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    if (hashTextField.text.isEmpty()) return@TextField
                    addHashTag(hashTextField.text.toString())
                    hashTextField.clearText()
                },
                shape = RoundedCornerShape(0.dp),
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    color = BlackColor
                ),
                colors = OneDayTextFieldColors,
                contentPadding = PaddingValues(0.dp)
            )   // 해시 태그 입력창
            Spacer(Modifier.height(10.dp))
            HashTagList(hashList) { deleteWord ->
                removeHashTag(deleteWord)
            }
        }
    }
}

@Preview
@Composable
fun HashTagListPreview() {
    HashTagList(
        listOf("123", "123123", "123", "13", "dsad3", "123", "vcxvxcvasfsaga", "123", "123"),
        onDeleteClick = {}
    )
}

@Composable
fun HashTagList(
    hashList: List<String>,
    onDeleteClick: (String) -> Unit
) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
//        items(hashList) {
        hashList.forEach {
            Row(
                modifier = Modifier
                    .clickable(onClick = { onDeleteClick.invoke(it) })
                    .background(color = SemiVioletColor, shape = RoundedCornerShape(5.dp))
                    .padding(horizontal = 4.dp, 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "# $it",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Black33Color
                    )
                )
                Spacer(Modifier.width(3.dp))
                Image(
                    modifier = Modifier.size(10.dp),
                    painter = painterResource(R.drawable.icon_close),
                    colorFilter = ColorFilter.tint(Black88Color),
                    contentDescription = null
                )
            }
        }
    }
}