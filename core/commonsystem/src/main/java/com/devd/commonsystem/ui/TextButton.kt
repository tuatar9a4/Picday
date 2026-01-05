package com.devd.commonsystem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    contentsPadding : PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
    textSize : TextUnit = 15.sp,
    onClick: () -> Unit
) {
    Text(
        modifier = modifier.then(
            Modifier
                .clickable(onClick = onClick)
                .background(
                    color = if (enable) AccentColor else AccentOpacity40Color,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(contentsPadding)
        ),
        textAlign = TextAlign.Center,
        text = text,
        style = OneDayTypography.bodyMedium.copy(
            fontSize = textSize,
            color = if (enable) WhiteColor else WhiteOpacity40Color
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
