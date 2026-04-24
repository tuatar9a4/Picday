package com.devd.commonsystem.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.theme.BlackF9Color
import com.devd.model.local.DiaryPhaseType

@Composable
fun ThemeIcon(
    modifier: Modifier = Modifier,
    themeType: DiaryPhaseType,
    showIndex: Int = 0
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .border(color = BlackF9Color, shape = CircleShape, width = 1.dp)
                .border(color = themeType.subColor, shape = CircleShape, width = 5.dp)
                .padding(5.dp)
                .border(color = BlackF9Color, shape = CircleShape, width = 1.dp)
                .clip(CircleShape),
            painter = painterResource(themeType.bg),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Image(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            painter = painterResource(themeType.ids.getOrNull(showIndex) ?: themeType.ids.first()),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}