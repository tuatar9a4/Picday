package com.devd.user.register.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.PrimaryColor
import com.devd.commonsystem.theme.TextDefaultColor
import com.devd.commonsystem.ui.TextButton
import com.devd.commonsystem.ui.Toolbar
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialog
import com.devd.commonsystem.ui.dialog.book.DiaryBookDialogType
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.utils.optimizeUriToFile
import com.devd.user.register.RegisterViewModel
import com.devd.user.register.data.RegisterStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterRoute(
    modifier: Modifier = Modifier,
    viewmodel: RegisterViewModel,
    onSnackBar: (String) -> Unit = {},
    backClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val currentStep = remember { mutableStateOf(RegisterStep.Step1) }
    val bookDialogInfo = viewmodel.diaryBookDialog.collectAsState()

    val messageDialog = viewmodel.simpleMessage.collectAsState(null)

    val loadingState = viewmodel.isLoading.collectAsState()

    LaunchedEffect(messageDialog.value) {
        messageDialog.value?.let { state ->
            onSnackBar(state.message)
            viewmodel.clearMessage()
        }
    }

    val changeStep = {
        when (currentStep.value) {
            RegisterStep.Step1 -> {
                if (!viewmodel.isCheckDuplicate.value) {
                    onSnackBar.invoke("중복 확인을 해주세요")
                } else {
                    currentStep.value = RegisterStep.Step2
                }
            }

            RegisterStep.Step2 -> currentStep.value = RegisterStep.Step3
            RegisterStep.Step3 -> viewmodel.showDiaryBookDialog()
        }
    }
    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(
                    color = PrimaryColor
                )
        )
    ) {
        Toolbar(
            titleBox = {
                Text(
                    "Register",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextDefaultColor)
                )
            },
            leftButtons = {
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                        .clickable(onClick = {
                            when (currentStep.value) {
                                RegisterStep.Step1 -> backClick.invoke()
                                RegisterStep.Step2 -> currentStep.value = RegisterStep.Step1
                                RegisterStep.Step3 -> currentStep.value = RegisterStep.Step2
                            }
                        }),
                    painter = painterResource(R.drawable.icon_back_arrow),
                    contentDescription = null
                )
            }
        )
        Spacer(Modifier.height(60.dp))
        when (currentStep.value) {
            RegisterStep.Step1 -> NickNameScreen(
                modifier = Modifier.weight(1f),
                editText = viewmodel.id,
                isCheckDuplicate = viewmodel.isCheckDuplicate,
                checkValidateId = viewmodel::checkValidateId,
                onDone = changeStep
            )

            RegisterStep.Step2 -> PasswordScreen(
                modifier = Modifier.weight(1f),
                passwordText = viewmodel.password,
                onSnackBarMessage = onSnackBar,
                onDone = changeStep
            )

            RegisterStep.Step3 -> InfoScreen(
                modifier = Modifier.weight(1f),
                nickName = viewmodel.nickname,
            )
        }
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            text = "Next",
            textSize = 20.sp,
            contentsPadding = PaddingValues(vertical = 15.dp, horizontal = 20.dp),
            onClick = changeStep
        )
        Spacer(Modifier.height(20.dp))
    }

    if (bookDialogInfo.value.isShow) {
        DiaryBookDialog(
            dialogType = DiaryBookDialogType.EDIT,
            bookInfo = bookDialogInfo.value.bookInfo,
            onDismissRequest = viewmodel::dismissBookDialog,
            onSaveClick = { imageUrl, title, description, monthType ,color->
                val uploadFile = imageUrl?.let { context.optimizeUriToFile(it) }
                viewmodel.saveAndMakeBookInfo(uploadFile, title, description, monthType)
            }
        )
    }

    loadingState.value.LoadingDialog()
}

@Preview
@Composable
fun NickNamePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = PrimaryColor)
    ) {
        NickNameScreen(
            editText = remember { mutableStateOf("") },
            isCheckDuplicate = remember { mutableStateOf(false) },
        )
    }
}

@Preview
@Composable
fun PasswordPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = PrimaryColor)
    ) {
        PasswordScreen(
            passwordText = remember { mutableStateOf("") },
            onSnackBarMessage = {}
        )

    }
}

@Preview
@Composable
fun InfoPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = PrimaryColor)
    ) {
        InfoScreen(

        )

    }
}