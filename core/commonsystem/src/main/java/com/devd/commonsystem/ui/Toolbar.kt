package com.devd.commonsystem.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.theme.WhiteColor

@Composable
fun Toolbar(
    title: String,
    @DrawableRes leftButtonIcon: Int = R.drawable.icon_back_arrow,
    leftButtonClick: (() -> Unit)? = null,
    @DrawableRes rightButtonIcon: Int = R.drawable.icon_hamburger,
    rightButtonClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = WhiteColor)
            .padding(horizontal = 15.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        leftButtonClick?.let {
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(leftButtonIcon),
                contentDescription = "Back"
            )
        } ?: Spacer(Modifier.size(32.dp))
        Text(
            text = title,
            style = OneDayTypography.titleMedium.copy(
                color = TextDefaultColor
            )
        )
        rightButtonClick?.let {
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(rightButtonIcon),
                contentDescription = "Back"
            )
        } ?: Spacer(Modifier.size(32.dp))
    }
}

@Preview
@Composable
fun ToolbarPreview() {
    Toolbar("Title")
}

@Preview
@Composable
fun ToolbarUseLeftPreview() {
    Toolbar("Title", leftButtonClick = {})
}

@Preview
@Composable
fun ToolbarUseRightPreview() {
    Toolbar("Title", rightButtonClick = {})
}

@Preview
@Composable
fun ToolbarUseBothPreview() {
    Toolbar("Title", leftButtonClick = {}, rightButtonClick = {})
}