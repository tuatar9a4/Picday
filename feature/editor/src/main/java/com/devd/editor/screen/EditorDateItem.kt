package com.devd.editor.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.utils.convertWeekStr
import com.devd.commonsystem.utils.noRippleClickable
import java.time.Instant
import java.time.ZoneId

@Composable
fun EditorDateItem(
    writeDate: Long = System.currentTimeMillis(),
    onShowCalendar: () -> Unit = {},
) {
    val dairyDate =
        remember(writeDate) { Instant.ofEpochMilli(writeDate).atZone(ZoneId.systemDefault()) }
    val day = dairyDate.dayOfMonth
    val month = dairyDate.monthValue
    val year = dairyDate.year
    val week = dairyDate.dayOfWeek.convertWeekStr()
    Row(
        modifier = Modifier
            .padding(start = 20.dp)
            .noRippleClickable(onClick = onShowCalendar),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day.toString(),
            style = OneDayTypography.titleLarge.copy(
                fontSize = 25.sp
            )
        )
        Spacer(Modifier.width(7.dp))
        Column {
            Text(
                text = "$month/$year", style = OneDayTypography.bodySmall
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = stringResource(week), style = OneDayTypography.bodySmall
            )
        }
        Spacer(Modifier.width(8.dp))
        Image(
            modifier = Modifier.size(15.dp),
            painter = painterResource(R.drawable.icon_drop_down),
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun EditorDateSreenPreview() {
    EditorScreen {_,_ -> }
}