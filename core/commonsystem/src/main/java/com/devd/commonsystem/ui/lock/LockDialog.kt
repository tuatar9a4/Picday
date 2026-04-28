package com.devd.commonsystem.ui.lock

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.Black33Color
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.RedColor
import com.devd.commonsystem.theme.VioletColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.utils.FontList
import com.devd.commonsystem.utils.noRippleClickable

enum class LockType {
    INPUT, REGISTER
}

@Preview
@Composable
fun LockDialogPrview() {
    LockDialog(
        type = LockType.INPUT,
        onDismissClick = {},
        inputFinish = {}
    )
}

@Composable
fun LockDialog(
    modifier: Modifier = Modifier,
    type: LockType = LockType.REGISTER,
    onDismissClick: () -> Unit,
    inputFinish: (String) -> Unit,
) {
    var password by remember { mutableStateOf("") }
    var rePassword by remember { mutableStateOf("") }

    var step by remember { mutableIntStateOf(0) }

    if(type == LockType.REGISTER){
        BackHandler() {
            onDismissClick()
        }
    }

    LaunchedEffect(password) {
        if (password.length == 4) {
            if (type == LockType.REGISTER) step++
            else inputFinish(password)
        }
    }

    LaunchedEffect(rePassword) {
        if (rePassword.length == 4) {
            if (rePassword != password) {
                step--
                rePassword = ""
                password = ""
            } else {
                inputFinish(password)
            }
        }
    }

    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(WhiteColor)
        ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Toolbar(
                rightButtons = {
                    if (type == LockType.REGISTER) Image(
                        modifier = Modifier
                            .size(36.dp)
                            .padding(5.dp)
                            .noRippleClickable(onClick = onDismissClick),
                        painter = painterResource(R.drawable.icon_close),
                        contentDescription = null
                    )
                }
            )
            Spacer(Modifier.height(20.dp))
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = "일기장 잠금 비밀번호 입력",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Black33Color
                )
            )
            Spacer(Modifier.height(10.dp))
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = if (step == 0) "비밀번호를 입력해주세요." else "비밀번호를 재 입력해주세요.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Black33Color
                )
            )
            if(type == LockType.REGISTER){
                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "❗비밀번호는 보안을 위해 외부로 전송되지 않아 분실시 찾을 수 없습니다!\n잊지 않도록 주의해주세요!",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = RedColor,
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(Modifier.height(50.dp))
            PinCodeIndicator(
                modifier = Modifier
                    .fillMaxWidth(),
                inputText = if (step == 0) password else rePassword,
                inputCount = if (step == 0) password.length - 1 else rePassword.length - 1
            )
        }
        PinKeypad(
            modifier = Modifier
                .padding(top = 20.dp),
            onNumberClick = {
                if (step == 0) {
                    if (password.length >= 4) return@PinKeypad
                    password += it
                } else {
                    if (rePassword.length >= 4) return@PinKeypad
                    rePassword += it
                }
            },
            onBackClick = {
                if (step == 0) password = password.substring(0, password.length - 1)
                else rePassword = rePassword.substring(0, rePassword.length - 1)
            },
            onClearClick = {
                if (step == 0) password = ""
                else rePassword = ""
            }
        )
    }
}

@Composable
fun PinCodeIndicator(
    modifier: Modifier = Modifier,
    pinLength: Int = 4,    // 전체 비밀번호 길이
    inputText: String,
    inputCount: Int        // 현재 입력된 숫자의 개수
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
    ) {
        repeat(pinLength) { index ->
            when {
                // 1. 이미 입력된 부분
                index < inputCount -> {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp)
                            .background(VioletColor, CircleShape)
                    )
                }
                // 2. 현재 입력해야 할 포커스 지점 (숫자 표시)
                index == inputCount -> {
                    Text(
                        modifier = Modifier.size(40.dp),
                        text = inputText[index].toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontList.STAR_DUST.fontFamily,
                            color = Black33Color,
                            fontSize = 22.sp
                        )
                    )
                }
                // 3. 아직 입력 안 된 부분
                else -> {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp)
                            .background(BlackD9Color, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun PinKeypad(
    modifier: Modifier = Modifier,
    onNumberClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val keys = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9),
        listOf(null, 0, null) // 하단 배열 (지우기, 0, CLEAR)
    )

    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp) // 행 간격
    ) {
        keys.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEachIndexed { index, key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clickable(
                                onClick = {
                                    when {
                                        key is Int -> onNumberClick(key)
                                        index == 0 -> onBackClick()
                                        index == 2 -> onClearClick()
                                    }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            // 숫자 버튼
                            key is Int -> {
                                Text(
                                    text = key.toString(),
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        color = Black33Color,
                                        fontSize = 28.sp
                                    )
                                )
                            }
                            // 왼쪽 하단: 지우기 아이콘 (이미지의 뒤로가기/지우기 모양)
                            rowIndex == 3 && index == 0 -> {
                                Image(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(R.drawable.icon_andgle_left),
                                    contentDescription = null
                                )
                            }
                            // 오른쪽 하단: CLEAR 텍스트
                            rowIndex == 3 && index == 2 -> {
                                Text(
                                    text = "CLEAR",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        color = Black33Color,
                                        fontSize = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun TestTextList() {
    Row(
        modifier = Modifier.height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Apple23",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontList.S_CORE_DREAM.fontFamily,
                color = Black33Color,
                fontSize = 22.sp
            )
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = "Apple23",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontList.MARUBURI.fontFamily,
                color = Black33Color,
                fontSize = 22.sp
            )
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = "Apple23",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontList.HUMAN_BEOMSEOK.fontFamily,
                color = Black33Color,
                fontSize = 22.sp
            )
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = "Apple23",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontList.STAR_DUST.fontFamily,
                color = Black33Color,
                fontSize = 22.sp
            )
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = "Apple2",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontList.STAR_DUST.fontFamily,
                color = Black33Color,
                fontSize = 22.sp
            )
        )
        Spacer(Modifier.width(5.dp))

    }
}