package com.devd.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.commonsystem.theme.SecondaryColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.utils.diaryPhaseIcon
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType

@Composable
fun BookCardScreen(
    modifier: Modifier = Modifier,
    bookInfo: DiaryBookInfo?,
    onBookClick: () -> Unit = {}
) {
    bookInfo ?: return
    Card(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .clickable(onClick = onBookClick)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = WhiteColor)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .background(color = WhiteColor, shape = RoundedCornerShape(5.dp)),
                        painter = bookInfo.bookPhaseType.diaryPhaseIcon(bookInfo.monthWritePercent),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(15.dp))
                    Column() {
                        Text(
                            modifier = Modifier.padding(end = 30.dp),
                            text = bookInfo.title.ifEmpty { "---" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = BlackColor
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            modifier = Modifier.padding(end = 30.dp),
                            text = bookInfo.description ?: "",
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                color = BlackColor
                            )
                        )
                    }
                }
            }
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                trackColor = GreyColor,
                color = SecondaryColor,
                gapSize = (-5).dp,
                strokeCap = StrokeCap.Round,
                drawStopIndicator = {},
                progress = {
                    bookInfo.monthWritePercent
                }
            )
            Spacer(Modifier.height(5.dp))
        }
    }
}

@Preview
@Composable
fun BookCardScreenPreview() {
    OneDayOneShotTheme {
        BookCardScreen(
            bookInfo = DiaryBookInfo(
                bookId = 3826,
                bookImage = null,
                title = "다이어리 타이틀",
                description = "다이어리 설명",
                bookPhaseType = DiaryPhaseType.MOON,
                createDate = 6782,
                monthWritePercent = 0.2f

            )
        )
    }
}