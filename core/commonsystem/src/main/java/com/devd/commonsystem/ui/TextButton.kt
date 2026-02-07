package com.devd.commonsystem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.AccentOpacity40Color
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.theme.WhiteOpacity40Color


@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    text: String,
    enableButtonColor : Color = AccentColor,
    disableButtonColor : Color = AccentOpacity40Color,
    contentsPadding : PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
    textSize : TextUnit = 15.sp,
    onClick: () -> Unit
) {
    Text(
        modifier = modifier.then(
            Modifier
                .clickable(onClick = onClick)
                .background(
                    color = if (enable) enableButtonColor else disableButtonColor,
                    shape = RoundedCornerShape(10.dp)
                )
                .wrapContentHeight(Alignment.CenterVertically)
                .padding(contentsPadding)
        ),
        textAlign = TextAlign.Center,
        text = text,
        style = OneDayTypography.bodyMedium.copy(
            fontSize = textSize,
            color = if (enable) WhiteColor else WhiteOpacity40Color,
            textAlign = TextAlign.Center
        )
    )
}


@Preview
@Composable
fun TextButtonEnablePreview() {
    TextButton(text = "text", enable = true, onClick = {})
}

@Preview
@Composable
fun TextButtonDisablePreview() {
    TextButton(text = "text", enable = false, onClick = {})
}
