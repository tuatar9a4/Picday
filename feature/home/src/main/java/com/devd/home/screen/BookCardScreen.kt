package com.devd.home.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.BlackF2Color
import com.devd.commonsystem.theme.BlackF9Color
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.commonsystem.theme.SubAccentColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.utils.diaryPhaseIcon
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType

@Composable
fun BookCardScreen(
    modifier: Modifier = Modifier,
    bookInfo: DiaryBookInfo?,
    onBookClick: () -> Unit = {}
) {
    bookInfo ?: return
    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = bookInfo.description ?: "",
            style = MaterialTheme.typography.titleSmall.copy(
                color = Black33Color,
            )
        )
        Spacer(Modifier.height(7.dp))
        Card(
            modifier = Modifier
                .clickable(onClick = onBookClick),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = BlackF9Color
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(15.dp)),
                        model = bookInfo.bookImage?.rememberImageUrl(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(10.dp))
                    Column() {
                        Text(
                            modifier = Modifier.padding(end = 15.dp),
                            text = bookInfo.title.ifEmpty { "---" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Black33Color
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .background(
                                    color = SubAccentColor,
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = BlackD9Color,
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .padding(horizontal = 7.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        color = AccentColor,
                                        shape = CircleShape
                                    )
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                modifier = Modifier,
                                text = "${bookInfo.continueWriteCount} 일 연속 기록 중",
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.displaySmall.copy(
                                    color = AccentColor
                                )
                            )

                        }
                    }
                }
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GradientCircularProgressIndicatorPreview(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        gradientColors = listOf(WhiteColor, AccentColor),
                        progress = bookInfo.monthWritePercent
                    )
                    Image(
                        modifier = Modifier.padding(10.dp),
                        painter = bookInfo.bookPhaseType.diaryPhaseIcon(bookInfo.monthWritePercent),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
fun GradientCircularProgressIndicatorPreview(
    modifier: Modifier = Modifier,
    progress: Float = 0.5f, // 0.0 ~ 1.0
    gradientColors: List<Color> = listOf(Color.Cyan, Color.Blue, Color.Magenta),
    trackColor: Color = Color.White,
    strokeWidth: Dp = 5.dp
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }

    Canvas(
        modifier = modifier
            .padding(strokeWidth / 2) // 스트로크가 잘리지 않도록 패딩
    ) {
        val startAngle = -90f
        val sweep = progress * 360f

        drawCircle(
            color = BlackF2Color,
            style = Fill
        )

        // 배경 트랙 (연한 회색 등)
        drawCircle(
            color = trackColor.copy(alpha = 0.3f),
            style = stroke
        )

        // 그라데이션 프로그레스
        drawArc(
            brush = Brush.linearGradient(
                colors = gradientColors
            ),
            startAngle = startAngle,
            sweepAngle = sweep,
            useCenter = false,
            style = stroke
        )
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