package com.devd.diary.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.WhiteColor

@Composable
fun DiaryContentsScreen(
    modifier: Modifier = Modifier,
    diaryDate: String,
    diaryContents: String,
    diaryTagList: List<String>
) {
    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .background(
                    color = BlackOpacity40Color,
                    shape = RoundedCornerShape(topEnd = 15.dp, topStart = 15.dp)
                )
                .padding(vertical = 10.dp, horizontal = 15.dp)
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HorizontalDivider(
            modifier = Modifier.size(width = 20.dp, 2.dp),
            thickness = 2.dp
        )
        Spacer(Modifier.height(5.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = diaryDate,
            style = OneDayTypography.labelLarge.copy(
                color = WhiteColor
            )
        )
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp),
            text = diaryContents,
            style = OneDayTypography.bodyLarge.copy(
                color = WhiteColor
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
                    modifier = Modifier
                        .border(1.dp, WhiteColor, RoundedCornerShape(20.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    text = "# $it",
                    style = OneDayTypography.bodyMedium.copy(
                        color = WhiteColor
                    )
                )
            }
        }
        Spacer(Modifier.height(20.dp))

    }
}