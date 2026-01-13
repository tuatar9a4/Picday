package com.devd.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.OneDayTypography

@Composable
fun YearCategory(
    modifier: Modifier = Modifier,
    year: Int,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = modifier.then(
            Modifier
                .clickable(onClick = { onClick.invoke(year) })
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "2026",
            style = OneDayTypography.bodyMedium.copy(
                color = BlackColor
            )
        )
        Spacer(Modifier.width(5.dp))
        Image(
            modifier = Modifier.size(14.dp),
            painter = painterResource(R.drawable.icon_drop_down),
            contentDescription = null
        )
    }

}

@Preview
@Composable
fun YearCategoryPreview() {
    YearCategory(
        modifier = Modifier,
        year = 2021
    ) { }
}