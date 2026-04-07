package com.devd.commonsystem.ui.dialog.book.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.model.local.DiaryPhaseType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookOptionScreen(
    modifier: Modifier,
    createDate: String,
    monthTypeState: MutableState<DiaryPhaseType>,
    dialogType: DiaryBookDialogType
) {

    var showMonthTypeSelectSheet by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.then(Modifier.fillMaxWidth()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            Row() {
                Text(text = "생성일 : ", style = OneDayTypography.labelLarge)
                Text(text = createDate, style = OneDayTypography.labelMedium)
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "MonthType", style = OneDayTypography.labelLarge)
            Spacer(Modifier.height(5.dp))
            Image(
                modifier = Modifier
                    .clickable(onClick = {
                        if (dialogType == DiaryBookDialogType.VIEW) return@clickable
                        showMonthTypeSelectSheet = true
                    })
                    .size(48.dp)
                    .border(2.dp, GreyColor, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                painter = painterResource(monthTypeState.value.ids.last()),
                contentDescription = null
            )
        }
    }

    if (showMonthTypeSelectSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showMonthTypeSelectSheet = false
            },
        ) {
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
            ) {
                Text("한달을 표현할 아이템을 선택해주세요")
                Spacer(Modifier.height(20.dp))
                DiaryPhaseType.entries.forEachIndexed { index, type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable {
                                monthTypeState.value = type
                                showMonthTypeSelectSheet = false
                            },
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        type.ids.forEach {
                            Image(
                                modifier = Modifier.size(28.dp),
                                painter = painterResource(it),
                                contentDescription = null
                            )
                        }
                        Spacer(Modifier.width(30.dp))
                        Image(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(R.drawable.icon_check),
                            colorFilter = ColorFilter.tint(AccentColor),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}