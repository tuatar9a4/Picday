package com.devd.commonsystem.ui.dialog.book.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackDDColor
import com.devd.commonsystem.theme.BlackF2Color
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.theme.bookColorList
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType
import com.devd.commonsystem.ui.dialog.book.screen.sheet.ColorThemeSheet
import com.devd.model.local.DiaryPhaseType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookOptionScreen(
    modifier: Modifier,
    bookColor: MutableState<Int> = remember { mutableIntStateOf(2) },
    monthTypeState: MutableState<DiaryPhaseType>,
    dialogType: DiaryBookDialogType
) {

    var showMonthTypeSelectSheet by remember { mutableStateOf(false) }
    var showBookColorSelectSheet by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.then(Modifier.fillMaxWidth()),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Theme", style = MaterialTheme.typography.labelSmall.copy(
                    color = BlackColor
                )
            )
            Spacer(Modifier.width(20.dp))
            Image(
                modifier = Modifier
                    .size(37.dp)
                    .then(
                        if (dialogType == DiaryBookDialogType.VIEW) Modifier
                        else Modifier
                            .clip(CircleShape)
                            .clickable(onClick = {
                                showMonthTypeSelectSheet = true
                            })
                    ),
                painter = painterResource(monthTypeState.value.ids.first()),
                contentDescription = null
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Color", style = MaterialTheme.typography.labelSmall.copy(
                    color = BlackColor
                )
            )
            Spacer(Modifier.width(20.dp))
            Box(
                modifier = Modifier
                    .size(37.dp)
                    .then(
                        if (dialogType == DiaryBookDialogType.VIEW) Modifier
                        else Modifier
                            .clip(CircleShape)
                            .clickable(onClick = {
                                showBookColorSelectSheet = true
                            })
                    )

            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.TopStart)
                        .border(1.dp, BlackDDColor, CircleShape)
                        .background(bookColorList[bookColor.value].first, shape = CircleShape)
                )
                Box(
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                        .align(Alignment.BottomEnd)
                        .size(19.dp)
                        .border(1.dp, BlackDDColor, CircleShape)
                        .background(bookColorList[bookColor.value].second, shape = CircleShape)
                )
            }
        }
    }

    if (showMonthTypeSelectSheet) {
        MonthThemeSheet(
            selectPos = monthTypeState.value.ordinal,
            onItemSelect = { selectItem ->
                showMonthTypeSelectSheet = false
                monthTypeState.value = selectItem
            }
        ) {
            showMonthTypeSelectSheet = false
        }
    }

    if (showBookColorSelectSheet) {
        ColorThemeSheet(
            selectPos = bookColor.value,
            onItemSelect = { selectPos ->
                showBookColorSelectSheet = false
                bookColor.value = selectPos
            }
        ) {
            showBookColorSelectSheet = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthThemeSheet(
    selectPos: Int,
    onItemSelect: (selectItem: DiaryPhaseType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        containerColor = WhiteColor,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Text(
                text = "한달을 표현할 아이템을 선택해주세요",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                DiaryPhaseType.entries.forEachIndexed { index, type ->
                    Box(
                        modifier = Modifier.clickable(onClick = {
                            onItemSelect(type)
                        })
                    ) {
                        Image(
                            modifier = Modifier.size(50.dp),
                            painter = painterResource(type.ids.first()),
                            contentDescription = null
                        )
                        if (selectPos == index) Image(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.TopEnd),
                            painter = painterResource(R.drawable.icon_check),
                            colorFilter = ColorFilter.tint(AccentColor),
                            contentDescription = null
                        )
                    }
                }
            }
            Spacer(Modifier.height(30.dp))
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                contentsPadding = PaddingValues(vertical = 20.dp),
                enableButtonColor = BlackF2Color,
                text = stringResource(R.string.close),
                textColor = BlackColor
            ) {
                onDismissRequest()
            }
        }
    }
}
