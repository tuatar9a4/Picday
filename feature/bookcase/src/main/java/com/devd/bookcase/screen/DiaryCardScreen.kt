package com.devd.bookcase.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.VioletColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.utils.dropShadow
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
    graphicsLayer : GraphicsLayer  = rememberGraphicsLayer(),
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                // 화면에도 보여야 하므로 실제로도 그립니다.
                drawLayer(graphicsLayer)
            }
            .padding(2.dp)
            .dropShadow(
                color = BlackColor,
                blur = 8.dp,
                spread = 1.dp,
                shape = RectangleShape
            )
            .background(color = WhiteColor)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = BlackColor)
                .aspectRatio(9 / 16f),
            model = diaryInfo.imageUrlList.firstOrNull()?.rememberImageUrl(),
            contentDescription = null,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp),

            ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                text = diaryInfo.content,
                style = MaterialTheme.typography.labelSmall.copy(color = Black33Color)
            )
            Spacer(Modifier.height(5.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                diaryInfo.tagList.forEach {
                    Text(
                        text = "#$it",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontStyle = FontStyle.Italic,
                            color = VioletColor
                        )
                    )
                }
            }
        }
    }
}