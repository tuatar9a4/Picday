package com.devd.commonsystem.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.AccentOpacity40Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.theme.GreyOpacity40Color
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.utils.getCurrentMonthRangeMillis
import java.time.LocalDate
import java.util.Calendar

@Preview
@Composable
fun CustomDatePickerDialog(
    title: String = "",
    initDateMillis: Long = Calendar.getInstance().timeInMillis,
    onSelectDate: (dateMillis: Long) -> Unit = {},
    onClickCancel: () -> Unit = {}
) {

    DatePickerDialog(
        onDismissRequest = { onClickCancel() },
        confirmButton = {},
        shape = RoundedCornerShape(10.dp)
    ) {
        val datePickerState = rememberDatePickerState(
            yearRange = 2026..2200,
            initialDisplayMode = DisplayMode.Picker,
            initialSelectedDateMillis = initDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val monthStartMillis = LocalDate.now().getCurrentMonthRangeMillis().first
                    return utcTimeMillis > monthStartMillis && utcTimeMillis < System.currentTimeMillis()

                }
            }
        )
        Column() {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContentColor = AccentColor,
                    selectedDayContainerColor = AccentOpacity40Color,
                    todayContentColor = AccentColor,
                    todayDateBorderColor = AccentColor,
                    disabledSelectedDayContentColor = GreyColor
                ),
                title = {
                    Text(
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                        text = title,
                        style = OneDayTypography.bodyLarge.copy(
                            color = BlackColor
                        )
                    )
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    enableButtonColor = GreyColor,
                    disableButtonColor = GreyOpacity40Color,
                    text = stringResource(R.string.cancel),
                    onClick = onClickCancel,
                )
                Spacer(modifier = Modifier.width(5.dp))
                TextButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    text = stringResource(R.string.confirm),
                    onClick = {
                        val selectMillis = datePickerState.selectedDateMillis ?: return@TextButton
                        onSelectDate.invoke(selectMillis)
                    }
                )
            }
        }
    }
}