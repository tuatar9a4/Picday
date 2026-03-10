package com.devd.commonsystem.ui.dialog

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.utils.bottomBorder
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.commonsystem.utils.rememberImagePicker
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType
import com.devd.permission.Consts
import com.devd.permission.IPermissionHandler
import com.devd.permission.rememberPermissionHandler
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class DiaryBookDialogType {
    VIEW, EDIT
}

@Preview
@Composable
fun DiaryBookDialogPreview() {
    DiaryBookDialog(
        dialogType = DiaryBookDialogType.EDIT,
        bookInfo = DiaryBookInfo(
            bookId = 7088,
            bookImage = "semper",
            title = "idque",
            description = "homero",
            bookPhaseType = DiaryPhaseType.MOON,
            createDate = 4104,
            monthWritePercent = 2.3f
        ),
        onDismissRequest = {},
        onSaveClick = { _, _, _, _ -> }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryBookDialog(
    dialogType: DiaryBookDialogType,
    bookInfo: DiaryBookInfo,
    onDismissRequest: () -> Unit,
    onSaveClick: ((imageUrl: Uri?, title: String, description: String, monthType: DiaryPhaseType) -> Unit)? = null
) {

    val scope = rememberCoroutineScope()
    val permissionHandler: IPermissionHandler = rememberPermissionHandler()

    val titleTextFieldState = rememberTextFieldState(bookInfo.title)
    val descriptionTextFieldState = rememberTextFieldState(bookInfo.description ?: "")
    var monthTypeState by remember { mutableStateOf(bookInfo.bookPhaseType) }
    var showMonthTypeSelectSheet by remember { mutableStateOf(false) }

    val createDate = remember {
        Instant.ofEpochMilli(bookInfo.createDate)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }

    var isShowImagePicker by remember { mutableStateOf(false) }
    var imageUrl: Any? by remember { mutableStateOf(bookInfo.bookImage) }
    val imagePicker = rememberImagePicker { uri ->
        imageUrl = uri
    }

    var isShowCoverImage by remember { mutableStateOf(false) }
    val contentsEditorBg = if (dialogType == DiaryBookDialogType.VIEW) {
        Modifier
    } else {
        Modifier.border(1.dp, BlackOpacity40Color, RoundedCornerShape(5.dp))
    }

    suspend fun checkPermission() {
        val grant = permissionHandler.requestPermissionIfNeeded(Consts.CAMERA_PERMISSION)
        if (!grant.any { !it.value }) {
            isShowImagePicker = true
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = WhiteColor
            )
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ((imageUrl as? Uri) ?: (imageUrl as? String)?.rememberImageUrl())?.let {
                        AsyncImage(
                            modifier = Modifier
                                .clickable(onClick = {
                                    if (dialogType == DiaryBookDialogType.VIEW) {
                                        isShowCoverImage = true
                                    } else {
                                        scope.launch { checkPermission() }
                                    }
                                })
                                .clip(RoundedCornerShape(5.dp))
                                .size(56.dp),
                            model = it,
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                    } ?: run {
                        Image(
                            modifier = Modifier
                                .clickable(onClick = {
                                    if (dialogType == DiaryBookDialogType.VIEW) return@clickable
                                    scope.launch { checkPermission() }
                                })
                                .border(1.dp, BlackColor, RoundedCornerShape(5.dp))
                                .size(56.dp)
                                .padding(10.dp),
                            painter = painterResource(R.drawable.icon_photo),
                            contentDescription = null
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    BasicTextField(
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (dialogType == DiaryBookDialogType.VIEW) Modifier
                                else Modifier.bottomBorder(1.dp, BlackOpacity40Color)
                            ),
                        state = titleTextFieldState,
                        textStyle = OneDayTypography.titleMedium.copy(
                            color = TextDefaultColor
                        ),
                        readOnly = dialogType == DiaryBookDialogType.VIEW,
                        inputTransformation = {
                            if (asCharSequence().length > 10) revertAllChanges()
                        }
                    )
                }
                Spacer(Modifier.height(15.dp))
                BasicTextField(
                    modifier = contentsEditorBg.then(
                        Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(5.dp)
                    ),
                    state = descriptionTextFieldState,
                    readOnly = dialogType == DiaryBookDialogType.VIEW,
                    textStyle = OneDayTypography.bodyMedium
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column() {
                        Row() {
                            Text(text = "생성일 : ", style = OneDayTypography.labelLarge)
                            Text(text = createDate, style = OneDayTypography.labelMedium)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "MonthType", style = OneDayTypography.labelLarge)
                        Spacer(Modifier.height(5.dp))
                        Image(
                            modifier = Modifier
                                .clickable(onClick = {
                                    if (dialogType == DiaryBookDialogType.VIEW) return@clickable
                                    showMonthTypeSelectSheet = true
                                })
                                .size(48.dp)
                                .border(2.dp, GreyColor, RoundedCornerShape(10.dp))
                                .padding(4.dp),
                            painter = painterResource(monthTypeState.ids.last()),
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.height(15.dp))
                Row() {
                    if (dialogType == DiaryBookDialogType.EDIT) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            enableButtonColor = GreyColor,
                            text = stringResource(R.string.cancel),
                            onClick = onDismissRequest
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    TextButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(if (dialogType == DiaryBookDialogType.EDIT) R.string.save else R.string.confirm),
                        onClick = {
                            onSaveClick?.invoke(
                                (imageUrl as? Uri),
                                titleTextFieldState.text.toString(),
                                descriptionTextFieldState.text.toString(),
                                monthTypeState
                            ) ?: onDismissRequest()
                        }
                    )

                }
            }
        }
    }
    if (showMonthTypeSelectSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showMonthTypeSelectSheet = false
            },
        ) {
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
            ) {
                Text("한달을 표현할 아이템을 선택해주세요")
                Spacer(Modifier.height(20.dp))
                DiaryPhaseType.entries.forEachIndexed { index, type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable {
                                monthTypeState = type
                                showMonthTypeSelectSheet = false
                            },
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        type.ids.forEach {
                            Image(
                                modifier = Modifier.size(28.dp),
                                painter = painterResource(it),
                                contentDescription = null
                            )
                        }
                        Spacer(Modifier.width(30.dp))
                        Image(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(R.drawable.icon_check),
                            colorFilter = ColorFilter.tint(AccentColor),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }

    if (isShowCoverImage) {
        ((imageUrl as? Uri) ?: (imageUrl as? String)?.rememberImageUrl())?.let { url ->
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
