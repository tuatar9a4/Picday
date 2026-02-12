package com.devd.commonsystem.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.WhiteColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ImagePickerDialog(
    onCameraClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        containerColor = WhiteColor,
        tonalElevation = 5.dp,
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier.clickable(onClick = {
                    onCameraClick.invoke()
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion { onDismiss.invoke() }
                }),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(R.drawable.icon_camera),
                    contentDescription = null
                )
                Spacer(Modifier.height(10.dp))
                Text("Camera")
            }
            Column(
                modifier = Modifier.clickable(onClick = {
                    onGalleryClick.invoke()
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion { onDismiss.invoke() }
                }),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(R.drawable.icon_photo),
                    contentDescription = null
                )
                Spacer(Modifier.height(10.dp))
                Text("Gallery")
            }
        }
    }
}


@Composable
fun Boolean.ShowImagePicker(
    onCameraClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    if (!this) return
    ImagePickerDialog(onCameraClick, onGalleryClick, onDismiss)
}