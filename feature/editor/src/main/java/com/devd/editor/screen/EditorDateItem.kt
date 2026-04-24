package com.devd.editor.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.utils.convertWeekStr
import com.devd.commonsystem.utils.noRippleClickable
import java.time.Instant
import java.time.ZoneId

@Composable
fun EditorDateItem(
    modifier: Modifier = Modifier,
    writeDate: Long = System.currentTimeMillis(),
    isCanChangeDate: Boolean = true,
    onShowCalendar: () -> Unit = {},
) {
    val dairyDate =
        remember(writeDate) { Instant.ofEpochMilli(writeDate).atZone(ZoneId.systemDefault()) }
    val day = dairyDate.dayOfMonth
    val month = dairyDate.monthValue
    val year = dairyDate.year
    val week = dairyDate.dayOfWeek.convertWeekStr()
    Row(
        modifier = modifier.then(
            Modifier
            .noRippleClickable(onClick = { if (isCanChangeDate) onShowCalendar() })
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$year.$month.$day", style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = stringResource(week), style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.width(8.dp))
//        날짜 선택 기능 비활성화
//        if (isCanChangeDate) {
//            Image(
//                modifier = Modifier.size(15.dp),
//                painter = painterResource(R.drawable.icon_drop_down),
//                contentDescription = null
//            )
//        }
    }
}

@Preview
@Composable
fun EditorDateScreenPreview() {
    EditorDateItem { }
}