package com.devd.editor.screen.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.VioletColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.utils.FeelData

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun FeelBottomSheet(
    selectIndex: Int = 0,
    onDismissRequest: () -> Unit = {},
    onItemClick: (Int) -> Unit = {}
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = onDismissRequest
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            verticalArrangement = Arrangement.spacedBy(33.dp),
            columns = GridCells.Fixed(4),
        ) {
            itemsIndexed(FeelData.feelList) { i, item ->
                Box(
                    modifier = Modifier.clickable(onClick = {
                        onItemClick(i)
                        onDismissRequest()
                    })
                ) {
                    Image(
                        modifier = Modifier.size(63.dp),
                        painter = painterResource(item),
                        contentDescription = null
                    )
                    if (i == selectIndex) {
                        Image(
                            modifier = Modifier
                                .background(WhiteColor, CircleShape)
                                .border(1.dp, BlackD9Color, CircleShape)
                                .size(22.dp)
                                .padding(5.dp)
                                .align(Alignment.BottomEnd),
                            painter = painterResource(R.drawable.icon_check),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(VioletColor)
                        )
                    }
                }
            }
        }
    }
}