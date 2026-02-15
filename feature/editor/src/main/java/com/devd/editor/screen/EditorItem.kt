package com.devd.editor.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.theme.OneDayTextFieldColors
import com.devd.commonsystem.theme.OneDayTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorItem(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    onChangeDiaryText: (String) -> Unit = {},
    onChangeTagItem: (List<String>) -> Unit = {},
    hashList: SnapshotStateList<String> = mutableStateListOf()
) {
    val hashTextField = rememberTextFieldState()

    fun addHashTag(hashTag: String) {
        hashList.add(hashTag)
        hashTextField.clearText()
        onChangeTagItem.invoke(hashList)
    }

    Column(
        modifier = modifier.then(Modifier)
    ) {
        Spacer(Modifier.height(5.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = with(LocalDensity.current) { (30.sp * 2).toDp() }),
            state = textFieldState,
            lineLimits = TextFieldLineLimits.MultiLine(1, 2),
            inputTransformation = {
                if (asCharSequence().lines().size > 2) revertAllChanges()
                onChangeDiaryText.invoke(asCharSequence().toString())
            },
            shape = RoundedCornerShape(0.dp),
            textStyle = OneDayTypography.bodyMedium,
            contentPadding = PaddingValues(10.dp),
            colors = OneDayTextFieldColors
        )   // 일기 내용 입력창
        Spacer(Modifier.width(10.dp))
        Row(
            modifier = Modifier.padding(start = 5.dp, top = 10.dp, end = 5.dp),
        ) {
            Text(
                modifier = Modifier.alignBy(FirstBaseline),
                text = "해시 태그", style = OneDayTypography.bodyMedium,
            )
            Spacer(Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alignByBaseline()
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 15.dp),
                    state = hashTextField,
                    lineLimits = TextFieldLineLimits.MultiLine(1, 1),
                    inputTransformation = {
                        if (length > 8) revertAllChanges()
                        if (asCharSequence().toString().contains(" ")) {
                            addHashTag(hashTextField.text.toString())
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    onKeyboardAction = {
                        addHashTag(hashTextField.text.toString())
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = OneDayTextFieldColors,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                )   // 해시 태그 입력창
                Spacer(Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(hashList) {
                        Text("#$it")
                    }
                }
            }
        }
    }
}