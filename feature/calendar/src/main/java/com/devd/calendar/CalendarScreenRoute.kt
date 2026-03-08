package com.devd.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.calendar.data.CalendarImageInfo
import com.devd.calendar.screen.CustomCalendarScreen
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.SecondaryColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.loading.LoadingDialog

@Composable
fun CalendarScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {

    val uiState by viewModel.calendarUiState.collectAsState()

    CalendarScreen(
        modifier = modifier,
        imageList = uiState.imageList,
        onBackClick = onBackClick
    )


    uiState.isLoading.LoadingDialog()
}


@Preview
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    imageList: List<CalendarImageInfo> = emptyList(),
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.then(
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
        )
    ) {
        Toolbar(
            title = "",
            leftButtonIcon = R.drawable.icon_back_arrow,
            leftButtonClick = onBackClick
        )
        Spacer(Modifier.height(5.dp))
        CustomCalendarScreen(
            infoList = imageList
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(color = SecondaryColor, shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 15.dp, vertical = 10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "완성률",
                    style = OneDayTypography.bodyMedium
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "0%",
                    style = OneDayTypography.labelMedium
                )
            }
        }
        Spacer(Modifier.height(20.dp))
    }

}