package com.devd.commonsystem.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Stable
class ImagePicker internal constructor(
    val launchCamera: () -> Unit,
    val launchGallery: () -> Unit
)

@Composable
fun rememberImagePicker(
    onImagePicked: (Uri?) -> Unit,
): ImagePicker {

    val context = LocalContext.current

    val currentOnImagePicked by rememberUpdatedState(onImagePicked)

    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentOnImagePicked(cameraUri)
            } else {
                currentOnImagePicked(null)
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            currentOnImagePicked(uri)
        }


    fun launchCamera() {
        cameraUri = context.createCameraUri()
        cameraUri?.let { cameraLauncher.launch(it) }

    }

    fun launchGallery() {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    return remember {
        ImagePicker(
            launchCamera = ::launchCamera,
            launchGallery = ::launchGallery
        )
    }
}
