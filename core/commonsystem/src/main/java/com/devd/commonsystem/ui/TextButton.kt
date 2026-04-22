package com.devd.commonsystem.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.AccentOpacity40Color
import com.devd.commonsystem.theme.WhiteColor


@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    @DrawableRes frontIcon: Int? = R.drawable.icon_plus,
    text: String,
    textColor: Color = WhiteColor,
    enableButtonColor: Color = AccentColor,
    disableButtonColor: Color = AccentOpacity40Color,
    borerColor: Color? = null,
    cornerRound : Dp = 20.dp,
    contentsPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
    textSize: TextUnit = 15.sp,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.then(
            Modifier
                .background(
                    color = if (enable) enableButtonColor else disableButtonColor,
                    shape = RoundedCornerShape(cornerRound)
                )
                .clip(RoundedCornerShape(cornerRound))
                .clickable(onClick = onClick)
                .wrapContentHeight(Alignment.CenterVertically)
                .then(
                    borerColor?.let {
                        Modifier.border(
                            1.dp,
                            color = borerColor,
                            RoundedCornerShape(cornerRound)
                        )
                    } ?: Modifier
                )
                .padding(contentsPadding)
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        frontIcon?.let {
            Image(
                modifier = Modifier.size(31.dp),
                painter = painterResource(it),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = textColor)
            )
        }
        Text(
            textAlign = TextAlign.Center,
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = textSize,
                color = if (enable) textColor else textColor.copy(0.4f),
                textAlign = TextAlign.Center
            )
        )
    }
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
