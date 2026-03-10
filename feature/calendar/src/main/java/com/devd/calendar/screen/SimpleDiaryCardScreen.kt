package com.devd.calendar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.devd.calendar.data.CalendarImageInfo
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.theme.textHashTagStyle


@Preview
@Composable
fun SimpleDiaryCardScreen(
    calendarImageInfo: CalendarImageInfo = CalendarImageInfo(
        day = 2649,
        isToday = false,
        isCurrentMonth = false,
        isSunDay = false,
        diaryId = 7479,
        imageStr = "accumsan",
        contents = "fusce",
        tagList = listOf("11")
    ),
    onBackPress: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onBackPress,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .aspectRatio(9 / 16f),
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlackColor)
            ) {
                calendarImageInfo.imageUrl()?.let {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = BlackColor, shape = RoundedCornerShape(20.dp)),
                        model = it,
                        contentDescription = null
                    )
                }
                Text(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .background(color = BlackOpacity40Color, shape = CircleShape)
                        .padding(vertical = 5.dp, horizontal = 15.dp),
                    text = calendarImageInfo.day.toString(),
                    style = OneDayTypography.bodyLarge.copy(
                        color = WhiteColor
                    )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(BlackOpacity40Color)
                        .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 10.dp),

                    ) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            minLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            text = calendarImageInfo.contents ?: "",
                            style = OneDayTypography.bodySmall.copy(
                                color = WhiteColor
                            )
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        calendarImageInfo.tagList?.forEach {
                            Text(
                                text = "# $it",
                                style = textHashTagStyle.copy(
                                    color = WhiteColor
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}