package com.devd.commonsystem.utils

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.core.content.FileProvider
import com.devd.commonsystem.R
import java.io.File


fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember {
            MutableInteractionSource()
        }) {
        onClick()
    }
}


fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width, y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

fun Context.createCameraUri(): Uri {
    val file = File(
        cacheDir,
        "camera_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        file
    )
}

@Composable
fun String.rememberImageUrl(): String {
    val ociKey = stringResource(R.string.ociBuketKey)
    return remember {
        "https://cnud835pjoeg.objectstorage.ap-seoul-1.oci.customer-oci.com/p/$ociKey/n/cnud835pjoeg/b/devd_storage/o/diary/$this"
    }
}