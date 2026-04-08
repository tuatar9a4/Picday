package com.devd.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.calendar.data.CalendarImageInfo
import com.devd.calendar.screen.CustomCalendarScreen
import com.devd.calendar.screen.MonthWritePercentScreen
import com.devd.calendar.screen.SimpleDiaryCardScreen
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.dialog.OptionBottomSheet
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.model.local.DiaryPhaseType
import com.devd.model.local.SheetItem
import java.time.YearMonth

@Composable
fun CalendarScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {

    val uiState by viewModel.calendarUiState.collectAsState()
    val dialogDiaryInfo = remember { mutableStateOf<CalendarImageInfo?>(null) }

    val isBookListSheet = viewModel.optionBookList.collectAsState()

    CalendarScreen(
        modifier = modifier,
        monthToList = uiState.monthToList,
        percentType = uiState.writeDisplayType,
        bookName = uiState.bookName,
        writePercent = uiState.writePercent,
        onShowBookList = { viewModel.fetchBookList() },
        onShowSimpleCard = { item -> dialogDiaryInfo.value = item },
        onChangeDate = viewModel::fetchDiaryImageWithMonth,
        onBackClick = onBackClick
    )

    if (dialogDiaryInfo.value != null) {
        SimpleDiaryCardScreen(dialogDiaryInfo.value!!) { dialogDiaryInfo.value = null }
    }
    if (isBookListSheet.value != null) {
        OptionBottomSheet(
            title = "Select Book",
            items = uiState.bookList.mapIndexed { index, info ->
                SheetItem(
                    id = info.bookId.toString(),
                    text = info.title,
                    isSelected = info.bookId == viewModel.bookId
                )
            },
            onItemSelected = { item ->
                viewModel.bookId = item.id.toLong()
                viewModel.fetchNewBookDiaryImage(item.text)
            },
            onDismissRequest = { viewModel.dismissBookList() }
        )
    }
    uiState.isLoading.LoadingDialog()
}


@Preview
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    monthToList: Map<YearMonth, List<CalendarImageInfo>> = emptyMap(),
    percentType: DiaryPhaseType = DiaryPhaseType.MOON,
    bookName: String = "",
    writePercent: Float = 0.5f,
    onShowSimpleCard: (CalendarImageInfo) -> Unit = {},
    onShowBookList: () -> Unit = {},
    onChangeDate: (selectMonth: Long, isPre: Boolean, isNext: Boolean) -> Unit = { _, _, _ -> },
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        )
    ) {
        Toolbar(
            titleBox = {
                Row(
                    modifier = Modifier.clickable(onClick = onShowBookList),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bookName,
                        style = MaterialTheme.typography.titleMedium.copy(color = BlackColor)
                    )
                    Spacer(Modifier.width(5.dp))
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.icon_drop_down),
                        contentDescription = null
                    )
                }
            },
            leftButtons = {
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                        .clickable(onClick = onBackClick),
                    painter = painterResource(R.drawable.icon_back_arrow),
                    contentDescription = null
                )
            }
        )
        Spacer(Modifier.height(5.dp))
        CustomCalendarScreen(
            infoList = monthToList,
            onDateSelector = onShowSimpleCard,
            onChangeDate = onChangeDate
        )
        Spacer(Modifier.height(10.dp))
        MonthWritePercentScreen(
            modifier = Modifier.padding(horizontal = 20.dp),
            type = percentType,
            percent = writePercent
        )
        Spacer(Modifier.height(20.dp))
    }

}
