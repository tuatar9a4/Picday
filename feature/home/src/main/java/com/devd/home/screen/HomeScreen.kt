package com.devd.home.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentOpacity40Color
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.Toolbar


@SuppressLint("ConfigurationScreenWidthHeight")
@Preview
@Composable
fun HomeScreenRoute(
    modifier: Modifier = Modifier,
    onEditorClick: () -> Unit = {},
//    viewModel: HomeViewModel = hiltViewModel()
) {

    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(color = PrimaryColor)
        )
    ) {
        Column() {
            Toolbar(
                modifier = Modifier.background(color = PrimaryColor),
                title = "",
                leftButtonIcon = R.drawable.icon_library,
                leftButtonClick = {},
                rightButtonIcon = R.drawable.icon_setting,
                rightButtonClick = {})
            Spacer(Modifier.height(20.dp))
            BookCardScreen(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                diaryTitle = "Diary Title",
                diaryDescription = "",
                diaryMonthPercent = 0.5f,
            )   // 일기장 카드
            Spacer(Modifier.height(20.dp))
            YearCategory(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                year = 2021,
                onClick = { year -> }
            )   // 년도 선택 스크린
            Spacer(Modifier.height(15.dp))
            DiaryListScreen(
                modifier = Modifier
            )   // DiaryList 스크린
        }
        Row(
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 40.dp)

        ) {
            FloatingActionButton(
                modifier = Modifier
                    .size(42.dp),
                shape = CircleShape,
                containerColor = AccentOpacity40Color,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                onClick = {},
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_pencil),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(WhiteColor)
                )
            }
            Spacer(Modifier.width(20.dp))
            FloatingActionButton(
                modifier = Modifier
                    .size(42.dp),
                shape = CircleShape,
                containerColor = AccentOpacity40Color,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                onClick = onEditorClick,
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_calendar),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(WhiteColor)
                )
            }
            Spacer(Modifier.width(20.dp))
        }
    }
}

