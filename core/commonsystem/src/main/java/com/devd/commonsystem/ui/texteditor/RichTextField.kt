package com.devd.commonsystem.ui.texteditor

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.devd.commonsystem.ui.texteditor.util.MarkdownVisualTransformer

@Preview
@Composable
fun MarkdownEditor(
    modifier: Modifier = Modifier
) {

    val text = remember { mutableStateOf(TextFieldValue("")) }
    Column(

    ) {
        TextField(
            value = text.value,
            onValueChange = { text.value = it },
            visualTransformation = MarkdownVisualTransformer()
        )
        Button(
            onClick = {
                text.value = insertBold(text.value)
            }
        ) {
            Text(text = "bold")
        }
    }
}

fun insertBold(value: TextFieldValue): TextFieldValue {
    val originText = value.text
    val selectionStart = value.selection.start
    val selectionEnd = value.selection.end

    val newText = originText.take(selectionStart) +
            "**" +
            originText.substring(selectionStart, selectionEnd) +
            "**" +
            originText.substring(selectionEnd)

    val newCursor = selectionStart + 2 + (selectionEnd - selectionStart)

    return TextFieldValue(
        text = newText,
        selection = TextRange(newCursor)
    )

}