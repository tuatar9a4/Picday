package com.devd.calendar.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.WhiteColor
import com.devd.model.local.DiaryPhaseType
import java.util.Locale

@Preview
@Composable
fun MonthWritePercentScreen(
    modifier: Modifier = Modifier,
    type: DiaryPhaseType = DiaryPhaseType.MOON,
    percent: Float = 0.05f
) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        )
    ) {
        CustomLinearProgress(
            modifier = Modifier.fillMaxSize(),
            progress = percent,
            activeColor = BlackColor
        )
        MultiMoonProgressBar(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp, vertical = 5.dp),
            iconList = type.ids,
            currentProgress = percent,
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Text(
                text = "완성률",
                style = OneDayTypography.bodyMedium.copy(
                    color = WhiteColor
                )
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "${String.format(Locale.US, "%.1f", percent * 100)} %",
                style = OneDayTypography.labelMedium.copy(
                    color = WhiteColor
                )
            )
        }
    }
}

@Preview
@Composable
fun MultiMoonProgressBar(
    modifier: Modifier = Modifier,
    iconList: IntArray = DiaryPhaseType.MOON.ids,
    currentProgress: Float = 0.47f, // 0.0 ~ 1.0
    activeColor: Color = Color(0xFFFFD700),
    inactiveColor: Color = GreyColor
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(iconList.size) { index ->
            // 각 아이콘이 담당할 progress 계산
            val iconProgress = (currentProgress * iconList.size - index).coerceIn(0f, 1f)

            MoonProgressIcon(
                moonIconRes = iconList[index],
                progress = iconProgress,
                completeColor = activeColor,
                emptyColor = inactiveColor,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun MoonProgressIcon(
    modifier: Modifier = Modifier,
    @DrawableRes moonIconRes: Int, // 생성한 달 아이콘 리소스
    progress: Float, // 0.0f ~ 1.0f
    completeColor: Color = Color(0xFFFFD700), // 차오를 색상 (예: 황금색)
    emptyColor: Color = Color(0xFFE0E0E0)    // 배경 달 색상 (예: 연회색)
) {
    val painter = painterResource(id = moonIconRes)

    Canvas(
        modifier = modifier
            .size(80.dp) // 원하는 크기 조절
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen) // 블렌드 모드 적용을 위해 필수
    ) {
        val width = size.width
        val height = size.height
        // 1. 배경이 되는 비어있는 달 그리기
        with(painter) {
            draw(
                size = size,
                colorFilter = ColorFilter.tint(emptyColor)
            )
        }
        // 2. 왼쪽에서 오른쪽으로 progress만큼 차오르는 영역 계산
        val progressWidth = width * progress

        // 3. 마스킹 적용하여 색상 입히기
        drawRect(
            color = completeColor,
            topLeft = Offset.Zero, // (0, 0) 지점부터 시작
            size = Size(progressWidth, height), // 가로 폭을 progress에 따라 조절
            blendMode = BlendMode.SrcIn
        )
    }
}

@Preview
@Composable
fun CustomLinearProgress(
    progress: Float = 0.05f,
    modifier: Modifier = Modifier,
    activeColor: Color = AccentColor,
    inactiveColor: Color = BlackColor.copy(alpha = 0.3f)
) {
    Canvas(
        modifier = modifier
            .height(12.dp)
            .fillMaxWidth()
    ) {
        val width = size.width
        val height = size.height
        val cornerRadius = CornerRadius(height, height)

        // 1. 전체 영역을 둥근 사각형(캡슐) 모양의 Path로 생성
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset.Zero, size),
                    cornerRadius = cornerRadius
                )
            )
        }

        // 2. 해당 Path로 Clipping (이 안에서 그리는 건 무조건 이 모양에 잘림)
        clipPath(path) {
            // 배경 그리기
            drawRect(
                color = inactiveColor,
                size = size
            )

            // 진행 바 그리기 (이제 직사각형으로 그려도 외곽선에 맞춰 잘림)
            drawRect(
                color = activeColor,
                size = Size(width * progress, height)
            )
        }
    }
}