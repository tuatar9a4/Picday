package com.devd.commonsystem.ui.dialog.book.screen.sheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.devd.commonsystem.utils.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorThemeSheet(
    selectPos: Int,
    onItemSelect: (selectItem: Int) -> Unit,
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
                text = "일기장의 색생을 정해주세요!",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(30.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                itemsIndexed(bookColorList) { i, (mainColor, subColor) ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .noRippleClickable(
                                onClick = {
                                    onDismissRequest()
                                    onItemSelect(i)
                                }
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.TopStart)
                                .border(1.dp, BlackDDColor, CircleShape)
                                .background(mainColor, shape = CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .padding(bottom = 3.dp)
                                .align(Alignment.BottomEnd)
                                .size(19.dp)
                                .border(1.dp, BlackDDColor, CircleShape)
                                .background(subColor, shape = CircleShape)
                        )
                        if (selectPos == i) Image(
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