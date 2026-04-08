package com.devd.bookcase.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.theme.textHashTagSmallStyle
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.model.local.DiaryInfo


@Preview
@Composable
fun DiaryCardScreenPreview() {
    DiaryCardScreen(
        diaryInfo = DiaryInfo(
            diaryId = 3709,
            diaryBookId = 6393,
            content = "turpis",
            mood = 3412,
            weather = 4676,
            createdAt = 8076,
            updatedAt = 7061,
            imageUrlList = listOf(),
            tagList = listOf("231", "124", "24")
        )
    )
}

@Composable
fun DiaryCardScreen(
    modifier: Modifier = Modifier,
    diaryInfo: DiaryInfo,
) {

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = BlackColor, shape = RoundedCornerShape(20.dp))
                .clip(shape = RoundedCornerShape(20.dp))
                .aspectRatio(9 / 16f),
            model = diaryInfo.imageUrlList.firstOrNull()?.rememberImageUrl(),
            contentDescription = null,
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .background(color = BlackOpacity40Color, shape = CircleShape)
                .padding(vertical = 5.dp, horizontal = 15.dp),
            text = diaryInfo.getDateStr("MM/dd"),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = WhiteColor
            )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    BlackOpacity40Color,
                    shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)
                )
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 20.dp),

            ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                text = diaryInfo.content,
                style = MaterialTheme.typography.labelSmall.copy(color = WhiteColor)
            )
            Spacer(Modifier.height(5.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                diaryInfo.tagList.forEach {
                    Text(
                        text = "# $it",
                        style = textHashTagSmallStyle.copy(
                            color = WhiteColor
                        )
                    )
                }
            }
        }
    }
}