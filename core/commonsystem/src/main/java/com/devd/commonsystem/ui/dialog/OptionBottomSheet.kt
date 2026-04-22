package com.devd.commonsystem.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackF2Color
import com.devd.commonsystem.ui.TextButton
import com.devd.model.local.SheetItem

@Preview
@Composable
fun OptionBottomSheetPreview() {
    OptionBottomSheet(
        title = "title",
        items = listOf(
            SheetItem(id = "0", text = "1", isSelected = true),
            SheetItem(id = "1", text = "2", isSelected = false),
            SheetItem(id = "2", text = "3", isSelected = false),
            SheetItem(id = "3", text = "4", isSelected = false)
        ),
        onItemSelected = {},
        onDismissRequest = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionBottomSheet(
    title: String,
    items: List<SheetItem>,
    isUseCloseButton: Boolean = false,
    onItemSelected: (SheetItem) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() } // 상단 핸들
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp), // 하단 여백
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            if (title.isNotEmpty()) Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            // Items List
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(items) { item ->
                    SheetItemRow(
                        item = item,
                        onClick = { onItemSelected(item) }
                    )
                }
            }
            if (isUseCloseButton) {
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
}

@Composable
fun SheetItemRow(
    item: SheetItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            item.itemIcon?.let {
                Image(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(it),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = item.itemColor)
                )
                Spacer(Modifier.width(7.dp))
            }
            // 왼쪽 Text
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = item.itemColor
                )
            )
        }

        // 오른쪽 CheckIcon (선택된 경우에만 표시)
        if (item.isSelected) {
            Image(
                painter = painterResource(R.drawable.icon_check),
                contentDescription = null
            )
        }
    }
}