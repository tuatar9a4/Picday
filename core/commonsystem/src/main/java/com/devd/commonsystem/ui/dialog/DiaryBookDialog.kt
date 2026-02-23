package com.devd.commonsystem.ui.dialog

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.utils.bottomBorder
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.commonsystem.utils.rememberImagePicker
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.model.local.DiaryPhaseType
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
        diaryImage = null,
        diaryTitle = "malesuada",
        diaryDescription = "ad",
        diaryCreateDate = 2620,
        diaryTotalCount = 4719,
        monthType = 0,
        onDismissRequest = {},
        onSaveClick = {}

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryBookDialog(
    dialogType: DiaryBookDialogType,
    diaryImage: String?,
    diaryTitle: String,
    diaryDescription: String,
    diaryCreateDate: Long,
    diaryTotalCount: Long,
    monthType: Int,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    val titleTextFieldState = rememberTextFieldState(diaryTitle)
    val descriptionTextFieldState = rememberTextFieldState(diaryDescription)
    var monthTypeState by remember { mutableStateOf(monthType) }
    var showMonthTypeSelectSheet by remember { mutableStateOf(false) }
    val createDate = remember {
        Instant.ofEpochMilli(diaryCreateDate)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }

    var isShowImagePicker by remember { mutableStateOf(false) }
    var imageUrl: Any? by remember { mutableStateOf(diaryImage) }
    val imagePicker = rememberImagePicker { uri ->
        println("CheckUri => $uri.. + => ${uri as? Uri}")
        imageUrl = uri
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ((imageUrl as? Uri) ?: (imageUrl as? String)?.rememberImageUrl())?.let {
                        println("CheckUri ??=> $it")
                        AsyncImage(
                            modifier = Modifier
                                .clickable(onClick = { isShowImagePicker = true })
                                .clip(RoundedCornerShape(5.dp))
                                .size(56.dp),
                            model = it,
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                    } ?: run {
                        Image(
                            modifier = Modifier
                                .clickable(onClick = { isShowImagePicker = true })
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
                            .bottomBorder(1.dp, BlackOpacity40Color),
                        state = titleTextFieldState,
                        textStyle = OneDayTypography.titleMedium,
                        readOnly = dialogType == DiaryBookDialogType.VIEW,
                        inputTransformation = {
                            if (asCharSequence().length > 10) revertAllChanges()
                        }
                    )
                }
                Spacer(Modifier.height(15.dp))
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(color = WhiteColor, shape = RoundedCornerShape(5.dp))
                        .padding(5.dp),
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
                        Spacer(Modifier.height(15.dp))
                        Row() {
                            Text(text = "총 일기 수 : ", style = OneDayTypography.labelLarge)
                            Text(text = "$diaryTotalCount", style = OneDayTypography.labelMedium)
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "MonthType", style = OneDayTypography.labelLarge)
                        Spacer(Modifier.height(5.dp))
                        Image(
                            modifier = Modifier
                                .clickable(onClick = { showMonthTypeSelectSheet = true })
                                .size(48.dp)
                                .border(2.dp, GreyColor, RoundedCornerShape(10.dp))
                                .padding(4.dp),
                            painter = painterResource(DiaryPhaseType.entries[monthTypeState].ids.last()),
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
                        onClick = onSaveClick
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
                                monthTypeState = index
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
    isShowImagePicker.ShowImagePicker(
        onCameraClick = imagePicker.launchCamera,
        onGalleryClick = imagePicker.launchGallery,
        onDismiss = { isShowImagePicker = false }
    )
}
