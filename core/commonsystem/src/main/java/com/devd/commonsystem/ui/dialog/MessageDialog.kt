package com.devd.commonsystem.ui.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.ui.TextButton

@Preview
@Composable
fun MessageDialog(
    message: String? = "Message",
    @StringRes leftButtonMessage: Int? = null,
    onLeftButtonClick: (() -> Unit)? = null,
    @StringRes rightButtonMessage: Int = R.string.confirm,
    onRightButtonClick: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 120.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))
            Text(
                text = message ?: "",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(30.dp))
            Row() {
                leftButtonMessage?.let {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        enableButtonColor = GreyColor,
                        contentsPadding = PaddingValues(vertical = 10.dp),
                        text = stringResource(it),
                        onClick = onLeftButtonClick ?: {}
                    )
                    Spacer(Modifier.width(10.dp))
                }
                TextButton(
                    modifier = Modifier.weight(1f),
                    contentsPadding = PaddingValues(vertical = 10.dp),
                    text = stringResource(rightButtonMessage),
                    onClick = onRightButtonClick
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun String?.ShowMessageDialog(
    onRightButtonClick: (() -> Unit),
    @StringRes rightButtonMessage: Int? = null,
    onLeftButtonClick: (() -> Unit)? = null,
    @StringRes leftButtonMessage: Int? = null
) {
    if (this == null) return
    MessageDialog(
        message = this,
        leftButtonMessage = leftButtonMessage,
        onLeftButtonClick = onLeftButtonClick,
        rightButtonMessage = rightButtonMessage ?: R.string.confirm,
        onRightButtonClick = onRightButtonClick
    )
}