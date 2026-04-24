package com.devd.home.screen

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.Black88Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.BlackF4Color
import com.devd.commonsystem.theme.BlackF9Color
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.commonsystem.ui.ThemeIcon
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryPhaseType

@Composable
fun BookCardScreen(
    modifier: Modifier = Modifier,
    bookInfo: DiaryBookInfo?,
    onBookClick: () -> Unit = {}
) {
    bookInfo ?: return
    val phaseIndex = ((bookInfo.bookPhaseType.ids.size - 1) * bookInfo.monthWritePercent).toInt()
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
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = bookInfo.title,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 13.dp, horizontal = 20.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThemeIcon(
                        modifier = Modifier.size(80.dp),
                        themeType = bookInfo.bookPhaseType,
                        showIndex = phaseIndex
                    )
                    Spacer(Modifier.width(10.dp))
                    Column() {
                        Text(
                            modifier = Modifier,
                            text = "stage $phaseIndex",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = bookInfo.bookPhaseType.mainColor
                            )
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            modifier = Modifier,
                            text = bookInfo.bookPhaseType.description.getOrNull(phaseIndex) ?: "-",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Black33Color,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            modifier = Modifier,
                            text = "\uD83D\uDD25 ${bookInfo.continueWriteCount}일 연속 기록 중",
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = Black88Color,
                            )
                        )
                    }
                }
                GradientCircularProgressIndicatorPreview(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    progress = bookInfo.monthWritePercent,
                    gradientColors = listOf(
                        bookInfo.bookPhaseType.subColor,
                        bookInfo.bookPhaseType.mainColor
                    ),
                )
                Spacer(Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    bookInfo.bookPhaseType.names.forEachIndexed { index, string ->
                        Text(
                            text = string,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (index < phaseIndex) bookInfo.bookPhaseType.mainColor
                                else if (index > phaseIndex) BlackD9Color
                                else BlackColor,
                                fontSize = 9.sp

                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GradientCircularProgressIndicatorPreview(
    modifier: Modifier = Modifier,
    progress: Float = 0.5f, // 0.0 ~ 1.0
    gradientColors: List<Color> = listOf(Color.Cyan, Color.Blue, Color.Magenta),
    trackColor: Color = BlackF4Color,
    strokeWidth: Dp = 5.dp
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth)
    ) {
        // 1. 전체 배경 (게이지가 차지할 빈 공간)
        drawRoundRect(
            cornerRadius = CornerRadius(size.height / 2f),
            color = trackColor,
            style = Fill
        )

        // 2. 실제 차오르는 프로그레스 (배경보다 위에 그림)
        val progressWidth = size.width * progress // 0.0f ~ 1.0f 사이 값

        drawRoundRect(
            cornerRadius = CornerRadius(size.height / 2f),
            brush = Brush.linearGradient(colors = gradientColors),
            size = Size(width = progressWidth, height = size.height),
            style = Fill
        )

//        // 3. 테두리 (가장 마지막에 그려서 위를 덮음)
//        drawRect(
//            color = YellowColor.copy(alpha = 0.3f),
//            style = stroke
//        )
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
                monthWritePercent = 0.6f

            )
        )
    }
}