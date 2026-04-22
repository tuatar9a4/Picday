package com.devd.bookcase.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.GreyOpacity40Color
import com.devd.commonsystem.theme.YellowColor
import com.devd.commonsystem.utils.getDateStr
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType

@Preview
@Composable
fun BookCoverItemPreview() {
    BookCoverItem(
        bookSize = IntSize(200, 300),
        bookInfo = DiaryBookInfo(
            bookId = 4658,
            bookColor = 5225,
            bookImage = "fusce",
            title = "egestas",
            description = "tibique",
            bookPhaseType = DiaryPhaseType.MOON,
            continueWriteCount = 5392,
            createDate = 4454,
            monthWritePercent = 2.3f,
            isMajor = false
        ),
        coverImage = "R.drawable.text_book_case_image",
        isOpen = false
    )
}

@Composable
fun BookCoverItem(
    modifier: Modifier = Modifier,
    bookSize: IntSize,
    bookInfo: DiaryBookInfo,
    coverImage: String?,
    isOpen: Boolean,
    onMoreClick: () -> Unit = {},
    onChangeMajor: () -> Unit = {}
) {
    val leftWidth = (bookSize.width * 0.125f).dp  // 왼쪽 면 너비
    val rightWidth = (bookSize.width * 0.875f).dp // 오른쪽 면 너비
    Column() {
        if (!isOpen) {
            BookHeader(
                modifier = Modifier.width(leftWidth + rightWidth),
                isMajor = bookInfo.isMajor,
                onChangeMajor = onChangeMajor,
                onMoreClick = onMoreClick
            )
            Spacer(Modifier.height(7.dp))
        }
        OpenableBook(
            modifier = modifier,
            bookSize = bookSize,
            bookImage = coverImage,
            onCloseBook = {}
        )
        if (!isOpen) {
            Column(
                modifier = Modifier
                    .width(leftWidth + rightWidth)
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(30.dp))
                Text(
                    text = bookInfo.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Black33Color
                    )
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = bookInfo.createDate.getDateStr("yyyy.MM.dd"),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BlackColor
                    )
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = bookInfo.description ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Black33Color
                    )
                )
            }
        }
    }
}


@Preview
@Composable
fun BookHeaderPreview() {
    BookHeader(isMajor = true, onMoreClick = {}, onChangeMajor = {})
}

@Composable
fun BookHeader(
    modifier: Modifier = Modifier,
    isMajor: Boolean,
    onMoreClick: () -> Unit,
    onChangeMajor: () -> Unit,
) {
    Row(
        modifier = modifier.then(Modifier.fillMaxWidth()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .size(30.dp)
                .noRippleClickable(
                    onClick = {
                        if (isMajor) return@noRippleClickable
                        onChangeMajor()
                    }
                ),
            painter = painterResource(R.drawable.icon_stroke_crown),
            contentDescription = null,
            colorFilter = ColorFilter.tint(if (isMajor) YellowColor else GreyOpacity40Color)
        )
        Image(
            modifier = Modifier
                .size(30.dp)
                .noRippleClickable(
                    onClick = onMoreClick
                ),
            painter = painterResource(R.drawable.icon_more),
            contentDescription = null,
            colorFilter = ColorFilter.tint(BlackColor)
        )

    }
}