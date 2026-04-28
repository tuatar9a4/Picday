package com.devd.intro.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.OneDayOneShotTheme
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialog
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.ui.lock.LockDialog
import com.devd.commonsystem.ui.lock.LockType
import com.devd.commonsystem.utils.optimizeUriToFile
import com.devd.intro.IntroViewModel

@Composable
fun IntroScreenRoute(
    modifier: Modifier,
    viewModel: IntroViewModel,
    onMakeDiaryClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {

    val context = LocalContext.current
    val uiState by viewModel.introUiState.collectAsState()
//    val isShowLoading by viewModel.isLoading.collectAsState()

    IntroScreen(
        modifier = modifier,
        onMakeDiaryClick = {
            viewModel.showDiaryBookDialog()
        },
        onLoginClick = onLoginClick
    )
    if (uiState.isShowBookDialog) {
        DiaryBookDialog(
            dialogType = DiaryBookDialogType.EDIT,
            bookInfo = viewModel.newBookInfo,
            onDismissRequest = {
                viewModel.dismissDiaryBookDialog()
            },
            onSaveClick = { imageUrl, title, description, monthType, color ->
                val uploadFile = imageUrl?.let { context.optimizeUriToFile(it) }
                viewModel.saveAndMakeBookInfo(uploadFile, title, description, monthType, color)
            }
        )
    }
    uiState.isLoading.LoadingDialog(message = "Checking ID...")
    if (uiState.isShowLockDialog != null) {
        LockDialog(
            modifier = modifier,
            type = LockType.INPUT,
            onDismissClick = {},
            inputFinish = { password ->
                if (uiState.isShowLockDialog == password) viewModel.passLockDiary()
                else viewModel.showUnMatchPassword()
            }
        )
    }
    uiState.simpleMessage?.messageStr?.ShowMessageDialog(
        onRightButtonClick = { viewModel.dismissSimpleMessageDialog() }
    )
}


@Preview
@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    uiState: MutableState<String> = mutableStateOf(""),
    onMakeDiaryClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(color = PrimaryColor)
        ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier)
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(48.dp),
                painter = painterResource(R.drawable.icon_diary),
//                painter = rememberVectorPainter(Icons.Rounded.Build),
                contentDescription = "로고 자리"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.intro_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextDefaultColor
                )
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = stringResource(R.string.intro_description),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextDefaultColor
                )
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = OneDayOneShotTheme.color.tertiary,
                ),
                onClick = onMakeDiaryClick
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 10.dp),
                    text = stringResource(R.string.make_diary_text),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = WhiteColor
                    )
                )
            }
//            Spacer(Modifier.height(10.dp))
//            Row(
//                modifier = Modifier.noRippleClickable(
//                    onClick = onLoginClick
//                ),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = stringResource(R.string.already_diary_text),
//                    style = MaterialTheme.typography.labelMedium.copy(
//                        color = TextDefaultColor
//                    )
//
//                )
//                Text(
//                    text = stringResource(R.string.login_text),
//                    style = MaterialTheme.typography.labelLarge.copy(
//                        color = AccentColor
//                    )
//                )
//
//            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}