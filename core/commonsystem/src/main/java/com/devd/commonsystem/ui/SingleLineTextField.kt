package com.devd.commonsystem.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.RedColor
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.theme.TextOpacity80Color
import com.devd.commonsystem.utils.bottomBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleLineTextField(
    modifier: Modifier = Modifier,
    editText: MutableState<String>,
    onTextChange: (String) -> Unit,
    isError: Boolean = false,
    textColor: Color = TextDefaultColor,
    @StringRes hintText: Int? = null,
    hintColor: Color = TextOpacity80Color,
    isPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.None,
    onDone: (() -> Unit)? = null,
    onNext: (() -> Unit)? = null
) {
    var usePasswordVisible by remember { mutableStateOf(!isPassword) }


    BasicTextField(
        modifier = modifier,
        value = editText.value,
        singleLine = true,
        textStyle = OneDayTypography.bodyMedium.copy(color = if (isError) RedColor else textColor),
        onValueChange = onTextChange,
        visualTransformation = if (usePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = { onDone?.invoke() },
            onNext = { onNext?.invoke() }
        ),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = editText.value,
                innerTextField = { innerTextField() },
                enabled = true,
                singleLine = true,
                placeholder = {
                    hintText?.let {
                        Text(
                            modifier = Modifier,
                            text = stringResource(it),
                            style = OneDayTypography.bodyMedium.copy(color = hintColor)
                        )
                    }
                },
                trailingIcon = {
                    if (isPassword) {
                        if (usePasswordVisible) {
                            Icon(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        usePasswordVisible = !usePasswordVisible
                                    }
                                ),
                                painter = painterResource(R.drawable.icon_show_password_eye),
                                tint = Color.Black,
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        usePasswordVisible = !usePasswordVisible
                                    }
                                ),
                                painter = painterResource(R.drawable.icon_hide_password_eye),
                                tint = Color.Black,
                                contentDescription = null
                            )
                        }
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = PaddingValues(vertical = 5.dp, horizontal = 25.dp),
                container = {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .bottomBorder(1.dp, if (isError) RedColor else BlackColor)
                            .padding(vertical = 10.dp, horizontal = 5.dp)
                    )
                }
            )
        }
    )
}

@Preview
@Composable
fun SingleLineTextFieldPreview() {
    val text = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val text2 = remember { mutableStateOf("") }
    val isError = remember { mutableStateOf(false) }
    Column {
        SingleLineTextField(
            modifier = Modifier.fillMaxWidth(),
            editText = text,
            hintText = R.string.intro_title,
            onTextChange = { str ->
                text.value = str
            },
            imeAction = ImeAction.Next,
            onNext = {
                focusRequester.requestFocus()
            }
        )
        SingleLineTextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth(),
            editText = text2,
            isError = isError.value,
            isPassword = true,
            hintText = R.string.intro_title,
            onTextChange = { str ->
                text2.value = str
                isError.value = text2.value.length < 6
            }
        )
    }
}