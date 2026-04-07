package com.devd.commonsystem.ui.dialog.book.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.bookColorList
import com.devd.commonsystem.utils.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ColorPalletIcon(
    modifier: Modifier = Modifier,
    bookColor: MutableState<Int> = remember { mutableIntStateOf(2) }
) {
    val selectColor = remember(bookColor) { bookColorList[bookColor.value].first }
    var isShowColorList by remember { mutableStateOf(false) }

    Image(
        modifier = modifier.then(
            Modifier
                .size(24.dp)
                .clickable(onClick = { isShowColorList = true })
        ),
        painter = painterResource(R.drawable.icon_color_pallet),
//        colorFilter = ColorFilter.tint(color = selectColor),
        contentDescription = null
    )


    if (isShowColorList) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            onDismissRequest = {
                isShowColorList = false
            },
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                itemsIndexed(bookColorList) { i, (mainColor, subColor) ->
                    Box(
                        modifier = Modifier.noRippleClickable(
                            onClick = {
                                bookColor.value = i
                                isShowColorList = false
                            }
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .border(
                                    2.dp,
                                    if (i == bookColor.value) AccentColor else BlackColor,
                                    CircleShape
                                )
                                .background(mainColor, shape = CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 10.dp, start = 10.dp)
                                .align(Alignment.BottomEnd)
                                .size(16.dp)
                                .border(1.dp, BlackColor, CircleShape)
                                .background(subColor, shape = CircleShape)
                        )
                    }
                }
            }
        }
    }
}