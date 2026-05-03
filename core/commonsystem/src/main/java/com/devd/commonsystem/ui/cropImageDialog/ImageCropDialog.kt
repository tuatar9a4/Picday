package com.devd.commonsystem.ui.cropImageDialog

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.utils.noRippleClickable
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ImageCropDialog(
    uri: Uri,
    onCropResult: (saveFile: File?) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageBitmap = remember { mutableStateOf(loadBitmapFromUri(context, uri)) }
    val containerSize = remember { mutableStateOf(IntSize.Zero) }
    val imageOffset = remember { mutableStateOf(Offset.Zero) }
    val cropRect = remember { mutableStateOf(Rect.Zero) }

    var isLoading by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {
            onCropResult.invoke(null)
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = BlackColor)
        ) {
            Column() {
                Toolbar(
                    titleBox = {
                        Text(
                            "Crop", style = MaterialTheme.typography.titleMedium.copy(
                                color = WhiteColor
                            )
                        )
                    },
                    leftButtons = {
                        Image(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(4.dp)
                                .clickable(onClick = { onCropResult.invoke(null) }),
                            painter = painterResource(R.drawable.icon_back_arrow),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = WhiteColor)
                        )
                    },
                    rightButtons = {
                        Image(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(4.dp)
                                .clickable(onClick = {
                                    if (isLoading) return@clickable
                                    isLoading = true
                                    scope.launch {
                                        val savedFile = context.cropAndSave(
                                            bitmap = imageBitmap.value,
                                            containerSize = containerSize.value,
                                            imageOffset = imageOffset.value,
                                            cropRect = cropRect.value
                                        )

//                                        isLoading = false
                                        onCropResult.invoke(savedFile)
                                    }
                                }),
                            painter = painterResource(R.drawable.icon_pencil),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = WhiteColor)
                        )
                    },
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
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable(onClick = {})
                        .background(color = BlackColor.copy(alpha = 0.5f)),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(120.dp).align(Alignment.Center),
                        color = ProgressIndicatorDefaults.circularColor.copy(
                            alpha = 1f,
                            red = 0.7647f,
                            blue = 0.5569f,
                            green = 0.7059f
                        )
                    )
                }
            }
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