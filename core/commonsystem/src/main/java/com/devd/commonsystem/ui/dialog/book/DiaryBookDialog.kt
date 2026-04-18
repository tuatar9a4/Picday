package com.devd.commonsystem.ui.dialog.book

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.ui.dialog.book.screen.BookDescriptionInput
import com.devd.commonsystem.ui.dialog.book.screen.BookImage
import com.devd.commonsystem.ui.dialog.book.screen.BookOptionScreen
import com.devd.commonsystem.ui.dialog.book.screen.BookTitleInput
import com.devd.model.local.DiaryBookInfo
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
        bookInfo = DiaryBookInfo(
            bookId = 1,
            bookImage = null,
            title = "",
            description = "",
            bookPhaseType = DiaryPhaseType.MOON,
            createDate = 4104,
            monthWritePercent = 1f
        ),
        onDismissRequest = {},
        onSaveClick = { _, _, _, _, _ -> }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryBookDialog(
    dialogType: DiaryBookDialogType,
    bookInfo: DiaryBookInfo,
    onDismissRequest: () -> Unit,
    onSaveClick: ((imageUrl: Uri?, title: String, description: String, monthType: DiaryPhaseType, color: Int) -> Unit)? = null
) {
    val bookColor = remember { mutableIntStateOf(bookInfo.bookColor) }

    val imageUrl: MutableState<Any?> = remember { mutableStateOf(bookInfo.bookImage) }

    val titleTextFieldState = rememberTextFieldState(bookInfo.title)
    val isErrorTitle = remember { mutableStateOf(false) }

    val descriptionTextFieldState = rememberTextFieldState(bookInfo.description ?: "")

    val monthTypeState = remember { mutableStateOf(bookInfo.bookPhaseType) }

    val createDate = remember {
        Instant.ofEpochMilli(bookInfo.createDate)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhiteColor, shape = RoundedCornerShape(20.dp)),
        ) {
            Column(
                modifier = Modifier.padding(top = 34.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                ) {
                    Row {
                        BookImage(
                            type = dialogType,
                            imageUrl = imageUrl
                        )
                        Spacer(Modifier.width(10.dp))
                        BookTitleInput(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.Bottom),
                            type = dialogType,
                            titleTextFieldState = titleTextFieldState,
                            isErrorTitle = isErrorTitle,
                        )
                    }
                    Row(
                        modifier = Modifier.align(Alignment.TopEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "생성일 : ", style = MaterialTheme.typography.labelLarge.copy(
                                color = BlackD9Color
                            )
                        )
                        Text(
                            text = createDate, style = MaterialTheme.typography.labelMedium.copy(
                                color = BlackD9Color
                            )
                        )
                    }
                }
                Spacer(Modifier.height(15.dp))
                BookDescriptionInput(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    type = dialogType,
                    descriptionTextFieldState = descriptionTextFieldState
                )
                Spacer(Modifier.height(10.dp))
                BookOptionScreen(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    monthTypeState = monthTypeState,
                    bookColor = bookColor,
                    dialogType = dialogType
                )
                Spacer(Modifier.height(15.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = GreyColor
                )
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    if (dialogType == DiaryBookDialogType.EDIT) {
                        TextButton(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 10.dp),
                            textColor = GreyColor,
                            enableButtonColor = WhiteColor,
                            text = stringResource(R.string.cancel),
                            onClick = onDismissRequest
                        )
                    }
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        thickness = 1.dp,
                        color = GreyColor
                    )
                    TextButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp),
                        text = stringResource(if (dialogType == DiaryBookDialogType.EDIT) R.string.save else R.string.confirm),
                        textColor = BlackColor,
                        enableButtonColor = WhiteColor,
                        onClick = {
                            if (titleTextFieldState.text.length < 2) {
                                isErrorTitle.value = true
                                return@TextButton
                            }
                            onSaveClick?.invoke(
                                (imageUrl.value as? Uri),
                                titleTextFieldState.text.toString(),
                                descriptionTextFieldState.text.toString(),
                                monthTypeState.value,
                                bookColor.intValue
                            ) ?: onDismissRequest()
                        }
                    )

                }
            }
        }
    }
}
