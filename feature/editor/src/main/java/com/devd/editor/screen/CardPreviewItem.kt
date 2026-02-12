package com.devd.editor.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.WhiteColor

@Composable
fun CardPreviewItem(
    imageUrl: Uri? = null, diaryText: String = "", diaryTag: List<String> = listOf()
) {
    val context = LocalContext.current

    val bitmap: Bitmap? = remember(imageUrl) {
        imageUrl?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 80.dp)
            .background(color = GreyColor, shape = RoundedCornerShape(10.dp))
            .aspectRatio(9 / 16f)
    ) {
        bitmap?.let {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxSize(),
                contentScale = ContentScale.Crop,
                bitmap = it.asImageBitmap(),
                contentDescription = null
            )
        } ?: run {
            Image(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(R.drawable.icon_photo),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    color = BlackOpacity40Color,
                    shape = RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp)
                )
                .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 10.dp),

            ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    text = diaryText,
                    style = OneDayTypography.bodySmall.copy(
                        color = WhiteColor
                    )
                )
            }
            Spacer(Modifier.height(5.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(diaryTag) {
                    Text(
                        text = "#$it",
                        style = OneDayTypography.labelLarge.copy(
                            color = WhiteColor
                        ),
                    )
                }
            }
        }
    }
}