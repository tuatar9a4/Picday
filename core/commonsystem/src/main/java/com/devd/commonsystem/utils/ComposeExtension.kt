package com.devd.commonsystem.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.devd.commonsystem.R
import com.devd.model.local.DiaryPhaseType
import java.io.File

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }
val LocalAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember {
            MutableInteractionSource()
        }) {
        onClick()
    }
}


@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width, y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

fun Context.createCameraUri(): Uri {
    val file = File(
        cacheDir,
        "camera_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        file
    )
}

@Composable
fun String.rememberImageUrl(): String {
    val ociKey = stringResource(R.string.ociBuketKey)
    return remember(this) {
        "https://cnud835pjoeg.objectstorage.ap-seoul-1.oci.customer-oci.com/p/$ociKey/n/cnud835pjoeg/b/devd_storage/o/diary/$this"
    }
}

@Composable
fun DiaryPhaseType.diaryPhaseIcon(
    @FloatRange(0.0, 1.0) percent: Float
): Painter {
    return when (this) {
        DiaryPhaseType.MOON -> {
            val index = ((this.ids.size - 1) * percent).toInt()
            painterResource(this.ids[index])
        }
    }
}

@Composable
fun SharedTransitionScope?.AnimateAsyncImage(
    modifier: Modifier,
    model: Any?,
    key: String,
    animatedVisibilityScope: AnimatedVisibilityScope?
) {
    AsyncImage(
        modifier = modifier.then(
            if (this == null || animatedVisibilityScope == null) {
                Modifier
            } else {
                with(this) {
                    Modifier.sharedElement(
                        rememberSharedContentState(key = key),
                        animatedVisibilityScope = animatedVisibilityScope,
                        renderInOverlayDuringTransition = false
                    )
                }
            }
        ),
        model = model,
        contentDescription = null
    )
}