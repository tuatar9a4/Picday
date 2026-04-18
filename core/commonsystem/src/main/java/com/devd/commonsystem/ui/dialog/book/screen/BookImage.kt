package com.devd.commonsystem.ui.dialog.book.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.ui.dialog.ShowImagePicker
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType
import com.devd.commonsystem.utils.rememberImagePicker
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.permission.Consts
import com.devd.permission.IPermissionHandler
import com.devd.permission.rememberPermissionHandler
import kotlinx.coroutines.launch

@Preview
@Composable
fun BookImageViewPreview() {
    BookImage(
        type = DiaryBookDialogType.VIEW,
        imageUrl = remember { mutableStateOf<Any?>(null) }
    )
}

@Preview
@Composable
fun BookImageEditPreview() {
    BookImage(
        type = DiaryBookDialogType.EDIT,
        imageUrl = remember { mutableStateOf<Any?>(null) }
    )
}

@Composable
fun BookImage(
    type: DiaryBookDialogType,
    imageUrl: MutableState<Any?>
) {
    val scope = rememberCoroutineScope()
    val permissionHandler: IPermissionHandler = rememberPermissionHandler()

    var isShowImagePicker by remember { mutableStateOf(false) }
    val imagePicker = rememberImagePicker { uri ->
        imageUrl.value = uri
    }
    var isShowCoverImage by remember { mutableStateOf(false) }

    suspend fun checkPermission() {
        val grant = permissionHandler.requestPermissionIfNeeded(Consts.CAMERA_PERMISSION)
        if (!grant.any { !it.value }) isShowImagePicker = true
    }


    ((imageUrl.value as? Uri) ?: (imageUrl.value as? String)?.rememberImageUrl())?.let {
        AsyncImage(
            modifier = Modifier
                .clickable(onClick = {
                    if (type == DiaryBookDialogType.VIEW) {
                        isShowCoverImage = true
                    } else {
                        scope.launch { checkPermission() }
                    }
                })
                .clip(RoundedCornerShape(10.dp))
                .size(88.dp),
            model = it,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    } ?: run {
        Image(
            modifier = Modifier
                .clickable(onClick = {
                    if (type == DiaryBookDialogType.VIEW) return@clickable
                    scope.launch { checkPermission() }
                })
                .border(1.dp, BlackColor, RoundedCornerShape(10.dp))
                .size(88.dp)
                .padding(10.dp),
            painter = painterResource(R.drawable.icon_photo),
            contentDescription = null
        )
    }

    //이미지 크게 확인
    if (isShowCoverImage) {
        ((imageUrl.value as? Uri) ?: (imageUrl.value as? String)?.rememberImageUrl())?.let { url ->
            Dialog(
                onDismissRequest = { isShowCoverImage = false }
            ) {
                AsyncImage(
                    modifier = Modifier
                        .padding(30.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    model = url,
                    contentDescription = null
                )
            }
        }
    }
    isShowImagePicker.ShowImagePicker(
        onCameraClick = imagePicker.launchCamera,
        onGalleryClick = imagePicker.launchGallery,
        onDismiss = { isShowImagePicker = false }
    )
}