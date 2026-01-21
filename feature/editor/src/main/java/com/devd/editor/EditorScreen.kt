package com.devd.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.BlackOpacity40Color
import com.devd.commonsystem.theme.GreyColor
import com.devd.commonsystem.theme.OneDayTextFieldColors
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.utils.convertWeekStr
import java.util.Calendar


@Composable
fun rememberImeVisible(): Boolean {
    return WindowInsets.ime.getBottom(LocalDensity.current) > 0
}

@Composable
fun rememberImeBottomSize(): Int {
    val density = LocalDensity.current
    return WindowInsets.ime.getBottom(density)
}

@Composable
fun EditorScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = hiltViewModel()
) {
    EditorScreen(
        modifier = modifier,
        onChangeDiaryText = viewModel::setDiaryText

    )
}

@Preview
@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    onChangeDiaryText : (String) -> Unit = {}
) {

    val textFieldState = rememberTextFieldState("")
    val hashTagList = remember { mutableStateListOf<String>() }
    val focusManger = LocalFocusManager.current
    val scrollState = rememberScrollState()

    //Keyboard 노출에 따른 Bottom Size
    val imeBottom = rememberImeBottomSize()
    LaunchedEffect(imeBottom) {
        if (imeBottom > 0) {
            scrollState.scrollBy(imeBottom.toFloat())
        } else {
            focusManger.clearFocus()
        }
    }

    //Keyboard 노출 여부
    val isKeyboardVisible = rememberImeVisible()
    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) {

        }
    }

    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
        )
    ) {
        Toolbar(
            title = "Editor",
            leftButtonIcon = R.drawable.icon_back_arrow,
            leftButtonClick = {})
        Spacer(Modifier.height(20.dp))
        EditorDateItem(

        )   // 날짜 View
        Spacer(Modifier.height(10.dp))
        CardPreviewItem(
            diaryText = textFieldState.text.toString(), diaryTag = hashTagList
        )   // 일기 미리보기
        Spacer(Modifier.height(10.dp))
        EditorItem(
            modifier = Modifier,
            textFieldState = textFieldState,
            onChangeDiaryText = onChangeDiaryText,
            hashList = hashTagList
        )   // 일기 작성
        Spacer(Modifier.height(20.dp))
    }

}

@Composable
fun EditorDateItem(
    date: Calendar = Calendar.getInstance()
) {
    val day = date.get(Calendar.DAY_OF_MONTH)
    val month = date.get(Calendar.MONTH) + 1
    val year = date.get(Calendar.YEAR)
    val week = date.get(Calendar.DAY_OF_WEEK).convertWeekStr()
    Row(
        modifier = Modifier.padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day.toString(), style = OneDayTypography.titleLarge.copy(
                fontSize = 25.sp
            )
        )
        Spacer(Modifier.width(7.dp))
        Column {
            Text(
                text = "$month/$year", style = OneDayTypography.bodySmall
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = stringResource(week), style = OneDayTypography.bodySmall
            )
        }
        Spacer(Modifier.width(5.dp))
        Image(
            modifier = Modifier.size(15.dp),
            painter = painterResource(R.drawable.icon_drop_down),
            contentDescription = null
        )
    }
}

@Composable
fun CardPreviewItem(
    imageUrl: String? = null, diaryText: String = "", diaryTag: List<String> = listOf()
) {
    val context = LocalContext.current

    val bitmap: Bitmap? = remember(imageUrl) {
        imageUrl?.let {
            context.contentResolver.openInputStream(it.toUri())?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 80.dp)
            .background(color = GreyColor, shape = RoundedCornerShape(5.dp))
            .aspectRatio(9 / 16f)
    ) {
        bitmap?.let {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
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
                .background(BlackOpacity40Color)
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
                        text = "#$it", style = OneDayTypography.labelLarge.copy(
                            color = WhiteColor
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorItem(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    onChangeDiaryText : (String) -> Unit = {},
    hashList: SnapshotStateList<String> = mutableStateListOf()
) {

    val hashTextField = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.then(Modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = WhiteColor)
                .padding(horizontal = 15.dp, vertical = 5.dp)
        ) {
            Text(text = "Bold", style = OneDayTypography.bodySmall)
        } // HTML 설정
        Spacer(Modifier.height(5.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = with(LocalDensity.current) { (30.sp * 2).toDp() }),
            state = textFieldState,
            lineLimits = TextFieldLineLimits.MultiLine(1, 2),
            inputTransformation = {
                if (asCharSequence().lines().size > 2) revertAllChanges()
                onChangeDiaryText.invoke(asCharSequence().toString())
            },
            shape = RoundedCornerShape(0.dp),
            textStyle = OneDayTypography.bodyMedium,
            contentPadding = PaddingValues(10.dp),
            colors = OneDayTextFieldColors
        )   // 일기 내용 입력창
        Spacer(Modifier.width(10.dp))
        Row(
            modifier = Modifier.padding(start = 5.dp, top = 10.dp, end = 5.dp),
        ) {
            Text(
                modifier = Modifier.alignBy(FirstBaseline),
                text = "해시 태그", style = OneDayTypography.bodyMedium,
            )
            Spacer(Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alignByBaseline()
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 15.dp),
                    state = hashTextField,
                    lineLimits = TextFieldLineLimits.MultiLine(1, 1),
                    inputTransformation = InputTransformation.maxLength(8),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    onKeyboardAction = {
                        hashList.add(hashTextField.text.toString())
                        hashTextField.clearText()
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = OneDayTextFieldColors,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                )   // 해시 태그 입력창
                Spacer(Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(hashList) {
                        Text("#$it")
                    }
                }
            }
        }
    }
}