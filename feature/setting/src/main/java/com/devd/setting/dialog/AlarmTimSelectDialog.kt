package com.devd.setting.dialog

import android.widget.NumberPicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.GreyColor
import java.util.Locale

@Preview
@Composable
fun AlarmTimeSelectDialogPreview() {
    AlarmTimSelectDialog(
        selectHour = 10,
        selectMin = 0,
        onDismissRequest = {},
        onConfirm = { _, _ -> }
    )
}

@Composable
fun AlarmTimSelectDialog(
    selectHour: Int,
    selectMin: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (hour: String, minute: String) -> Unit
) {
    // 선택된 시간을 관리하는 상태
    var selectedHour by remember { mutableIntStateOf(selectHour) }
    var selectedMinute by remember { mutableIntStateOf(selectMin) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        confirmButton = {
            TextButton(
                onClick = {
                    val hourStr=String.format(Locale.US,"%02d", selectedHour)
                    val minStr=String.format(Locale.US,"%02d", selectedMinute)
                    onConfirm(hourStr, minStr)
                }
            ) {
                Text(
                    stringResource(R.string.confirm),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = AccentColor
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    stringResource(R.string.cancel),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = GreyColor
                    )
                )
            }
        },
        title = {
            Text(
                text = "알람 시간 선택",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "알람 시간을 선택해 주세요\n해당 시간에 Push 알람을 보냅니다.",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 시간/분 선택 스크롤 UI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 시간 선택 (0-23)
                    ScrollNumberPicker(
                        minValue = 0,
                        maxValue = 23,
                        currentValue = selectedHour,
                        onValueChange = { selectedHour = it }
                    )

                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // 분 선택 (0-59)
                    ScrollNumberPicker(
                        minValue = 0,
                        maxValue = 1,
                        displayedValues = arrayOf("00", "30"),
                        currentValue = if (selectedMinute >= 30) 1 else 0,
                        onValueChange = {
                            selectedMinute = if (it == 1) 30 else 0
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun ScrollNumberPicker(
    minValue: Int,
    maxValue: Int,
    displayedValues: Array<String>? = null,
    currentValue: Int,
    onValueChange: (Int) -> Unit
) {
    AndroidView(
        modifier = Modifier.width(60.dp),
        factory = { context ->
            NumberPicker(context).apply {
                this.minValue = minValue
                this.maxValue = maxValue
                this.value = currentValue
                displayedValues?.let { this.displayedValues = it }
                // 01, 02 형식으로 보이게 설정 (필요 시)
                setFormatter { String.format("%02d", it) }
                setOnValueChangedListener { _, _, newVal ->
                    onValueChange(newVal)
                }
            }
        },
        update = { picker ->
            picker.value = currentValue
        }
    )
}