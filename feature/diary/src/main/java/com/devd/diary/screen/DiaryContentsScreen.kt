package com.devd.diary.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.Black88Color
import com.devd.commonsystem.theme.VioletColor
import com.devd.commonsystem.theme.WhiteColor

@Composable
fun DiaryContentsScreen(
    modifier: Modifier = Modifier,
    diaryDate: String,
    diaryContents: String,
    diaryFeel : Int?,
    diaryTagList: List<String>
) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .background(color = WhiteColor)
                .padding(vertical = 10.dp, horizontal = 15.dp)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = diaryDate,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Black88Color
                )
            )
            Spacer(Modifier.height(10.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp),
                text = diaryContents,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Black33Color
                )
            )
            Spacer(Modifier.height(15.dp))
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                diaryTagList.forEach {
                    Text(
                        text = "#$it",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = VioletColor
                        )
                    )
                }
            }
        }
        diaryFeel?.let {
            Image(
                modifier = Modifier.size(36.dp).align(Alignment.TopEnd),
                painter = painterResource(diaryFeel),
                contentDescription = null
            )
        }
    }
}