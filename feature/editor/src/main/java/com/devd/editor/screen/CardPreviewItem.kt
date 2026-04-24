package com.devd.editor.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.BlackF4Color
import com.devd.commonsystem.theme.BlackOpacity15Color
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.TransParents
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.utils.noRippleClickable
import com.devd.commonsystem.utils.rememberImageUrl
import com.devd.editor.data.ImageType
import com.devd.editor.data.Local
import com.devd.editor.data.Remote
import timber.log.Timber

@Composable
fun CardPreviewItem(
    imageUrl: ImageType? = null,
    diaryContents: String = "",
    diaryTag: List<String> = listOf(),
    onChangeImage: () -> Unit = {},
) {
    val context = LocalContext.current
    val bitmap: Bitmap? = remember(imageUrl) {
        (imageUrl as? Local)?.uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
    }
    Timber.d("CheckRetmoe => ${imageUrl is Remote} => ${(imageUrl as? Remote)?.url?.rememberImageUrl()}")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 100.dp)
            .background(color = BlackF4Color, shape = RoundedCornerShape(10.dp))
            .aspectRatio(9 / 16f)
    ) {
        imageUrl?.let {
            AsyncImage(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxSize()
                    .noRippleClickable(onClick = onChangeImage),
                contentScale = ContentScale.Crop,
                model = if (imageUrl is Remote) imageUrl.url?.rememberImageUrl() else bitmap,
                contentDescription = null
            )
        } ?: run {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(36.dp)
                    .noRippleClickable(onClick = onChangeImage),
                painter = painterResource(R.drawable.icon_camera),
                colorFilter = ColorFilter.tint(BlackD9Color),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to TransParents,
                            0.5f to BlackOpacity15Color,
                            1.0f to BlackOpacity40Color,
                        ),
                    ),
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
                    text = diaryContents,
                    style = MaterialTheme.typography.bodySmall.copy(
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
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontStyle = FontStyle.Italic,
                            color = WhiteColor
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CardPreviewScreenPreview() {
    CardPreviewItem()
}