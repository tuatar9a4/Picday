package com.devd.commonsystem.ui.cropImageDialog

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.ui.Toolbar
import java.io.File

@Composable
fun ImageCropDialog(
    uri: Uri,
    onCropResult: (saveFile: File?) -> Unit,
) {
    val context = LocalContext.current
    val imageBitmap = remember { mutableStateOf(loadBitmapFromUri(context, uri)) }
//    val imageBitmap = remember { mutableStateOf(createRainbowBitmap(1080, 1900)) }
    val containerSize = remember { mutableStateOf(IntSize.Zero) }
    val imageOffset = remember { mutableStateOf(Offset.Zero) }
    val cropRect = remember { mutableStateOf(Rect.Zero) }
    Dialog(
        onDismissRequest = {
            onCropResult.invoke(null)
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = BlackColor)
        ) {
            Toolbar(
                title = "Crop",
                useWhitIcon = true,
                leftButtonClick = {
                    onCropResult.invoke(null)
                },
                rightButtonIcon = R.drawable.icon_pencil,
                rightButtonClick = {
                    val savedFile = context.cropAndSave(
                        bitmap = imageBitmap.value,
                        containerSize = containerSize.value,
                        imageOffset = imageOffset.value,
                        cropRect = cropRect.value
                    )
                    onCropResult.invoke(savedFile)
                }
            )
            Spacer(Modifier.height(30.dp))
            CropImageBox(
                modifier = Modifier
                    .weight(1f),
                imgBitmap = imageBitmap.value,
                containerSize = containerSize,
                imageOffset = imageOffset,
                cropRect = cropRect
            )
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Preview
@Composable
fun ImageCropDialogPreview() {
    ImageCropDialog(
        uri = Uri.EMPTY,
        onCropResult = {}
    )
}

@Composable
fun Uri.ShowCropDialog(
    onCropResult: (saveFile: File?) -> Unit
) {
    ImageCropDialog(
        uri = this,
        onCropResult = onCropResult
    )
}