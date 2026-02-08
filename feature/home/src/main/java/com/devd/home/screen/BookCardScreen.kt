package com.devd.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.SecondaryColor
import com.devd.commonsystem.theme.WhiteColor

@Composable
fun BookCardScreen(
    modifier: Modifier = Modifier,
    diaryTitle: String = "",
    diaryDescription: String = "",
    diaryMonthPercent: Float = 0f
) {
    Card(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
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
                        painter = painterResource(R.drawable.icon_diary_book),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(15.dp))
                    Column() {
                        Text(
                            modifier = Modifier.padding(end =  30.dp),
                            text = diaryTitle.ifEmpty { "---" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = OneDayTypography.bodyLarge.copy(
                                color = BlackColor
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            modifier = Modifier.padding(end = 30.dp),
                            text = diaryDescription,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = OneDayTypography.bodySmall.copy(
                                fontSize = 13.sp,
                                color = BlackColor
                            )
                        )
                    }
                }
                Image(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd),
                    painter = painterResource(R.drawable.icon_diary),
                    contentDescription = null
                )

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
                    diaryMonthPercent
                }
            )
            Spacer(Modifier.height(5.dp))
        }
    }
}

@Preview
@Composable
fun BookCardScreenPreview(){
    OneDayOneShotTheme{
        BookCardScreen(
            diaryTitle = "다이어리 타이틀",
            diaryDescription = "다이어리 설명",
            diaryMonthPercent = 0.2f
        )
    }
}