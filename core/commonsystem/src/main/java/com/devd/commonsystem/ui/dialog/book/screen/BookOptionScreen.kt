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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.BlackF2Color
import com.devd.commonsystem.theme.BlackF9Color
import com.devd.commonsystem.theme.WhiteColor
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

    Column(
        modifier = modifier.then(Modifier.fillMaxWidth()),
    ) {
        Text(
            text = "내 일기장과 함께 할 테마를 선택해주세요",
            style = MaterialTheme.typography.labelSmall.copy(color = Black33Color)
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = {
                    if (dialogType == DiaryBookDialogType.EDIT) showMonthTypeSelectSheet = true
                })
                .size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .border(color = BlackF9Color, shape = CircleShape, width = 1.dp)
                    .border(color = monthTypeState.value.color, shape = CircleShape, width = 5.dp)
                    .padding(5.dp)
                    .border(color = BlackF9Color, shape = CircleShape, width = 1.dp)
                    .clip(CircleShape),
                painter = painterResource(monthTypeState.value.bg),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Image(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                painter = painterResource(monthTypeState.value.ids.first()),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            if (dialogType == DiaryBookDialogType.EDIT) Image(
                modifier = Modifier
                    .size(23.dp)
                    .background(color = WhiteColor, shape = CircleShape)
                    .border(1.dp, BlackD9Color, CircleShape)
                    .align(Alignment.BottomEnd)
                    .padding(5.dp),
                painter = painterResource(R.drawable.icon_pencil),
                colorFilter = ColorFilter.tint(color = Black33Color),
                contentDescription = null
            )
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DiaryPhaseType.entries.forEachIndexed { index, type ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable(onClick = { onItemSelect(type) }),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(color = BlackF9Color, shape = CircleShape, width = 1.dp)
                                .border(color = type.color, shape = CircleShape, width = 5.dp)
                                .padding(5.dp)
                                .border(color = BlackF9Color, shape = CircleShape, width = 1.dp)
                                .clip(CircleShape),
                            painter = painterResource(type.bg),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        Image(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            painter = painterResource(type.ids.first()),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
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
