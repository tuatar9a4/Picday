package com.devd.commonsystem.ui

import androidx.compose.foundation.Image
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

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    titleBox: @Composable (() -> Unit)? = null,
    leftButtons: @Composable (() -> Unit)? = null,
    rightButtons: @Composable (() -> Unit)? = null,
) {

    Row(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp)
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        leftButtons?.invoke() ?: Spacer(Modifier.size(32.dp))

        titleBox?.invoke()

        rightButtons?.invoke() ?: Spacer(Modifier.size(32.dp))
    }
}

@Preview
@Composable
fun ToolbarPreview() {
    Toolbar(
        titleBox = {
            Text(
                "Title", style = OneDayTypography.titleMedium.copy(
                    color = TextDefaultColor
                )
            )
        }
    )
}

@Preview
@Composable
fun ToolbarUseLeftPreview() {
    Toolbar(
        titleBox = {
            Text(
                "Title", style = OneDayTypography.titleMedium.copy(
                    color = TextDefaultColor
                )
            )
        },
        leftButtons = {
            Image(
                painter = painterResource(R.drawable.icon_back_arrow),
                contentDescription = null
            )
        }
    )
}

@Preview
@Composable
fun ToolbarUseRightPreview() {
    Toolbar(titleBox = {
        Text(
            "Title", style = OneDayTypography.titleMedium.copy(
                color = TextDefaultColor
            )
        )
    }, rightButtons = {
        Image(
            painter = painterResource(R.drawable.icon_delete_trash),
            contentDescription = null
        )
    })
}

@Preview
@Composable
fun ToolbarUseBothPreview() {
    Toolbar(titleBox = {
        Text(
            "Title", style = OneDayTypography.titleMedium.copy(
                color = TextDefaultColor
            )
        )
    }, leftButtons = {
        Image(
            painter = painterResource(R.drawable.icon_back_arrow),
            contentDescription = null
        )
    }, rightButtons = {
        Image(
            painter = painterResource(R.drawable.icon_delete_trash),
            contentDescription = null
        )
    })
}