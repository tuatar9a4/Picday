package com.devd.bookcase.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.WhiteColor


@Preview
@Composable
fun DiaryBookActionButtonPreview() {
    DiaryBookActionButton(
        onEditDiaryBook = {},
        onAddDiaryBook = {},
        onDeleteDiaryBook = {}
    )
}


@Composable
fun DiaryBookActionButton(
    onEditDiaryBook: () -> Unit,
    onAddDiaryBook: () -> Unit,
    onDeleteDiaryBook: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally)
    ) {
        Image(
            modifier = Modifier
                .shadow(elevation = 1.dp, shape = CircleShape)
                .background(WhiteColor, CircleShape)
                .size(52.dp)
                .clickable(onClick = onEditDiaryBook)
                .padding(14.dp),
            painter = painterResource(R.drawable.icon_pencil),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .shadow(elevation = 1.dp, shape = CircleShape)
                .background(WhiteColor, CircleShape)
                .size(52.dp)
                .clickable(onClick = onAddDiaryBook)
                .padding(14.dp),
            painter = painterResource(R.drawable.icon_plus),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .shadow(elevation = 1.dp, shape = CircleShape)
                .background(WhiteColor, CircleShape)
                .size(52.dp)
                .clickable(onClick = onDeleteDiaryBook)
                .padding(14.dp),
            painter = painterResource(R.drawable.icon_delete_trash),
            contentDescription = null
        )
    }

}