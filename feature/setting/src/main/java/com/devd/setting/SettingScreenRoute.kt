package com.devd.setting

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.BlackD9Color
import com.devd.commonsystem.theme.TransParents
import com.devd.commonsystem.theme.VioletColor
import com.devd.commonsystem.theme.WhiteColor
import com.devd.commonsystem.ui.dialog.OptionBottomSheet
import com.devd.commonsystem.ui.dialog.ShowMessageDialog
import com.devd.commonsystem.ui.loading.LoadingDialog
import com.devd.commonsystem.ui.lock.LockDialog
import com.devd.commonsystem.ui.lock.LockType
import com.devd.commonsystem.utils.FontList
import com.devd.model.local.SheetItem
import com.devd.permission.Consts
import com.devd.permission.IPermissionHandler
import com.devd.permission.rememberPermissionHandler
import com.devd.setting.data.ItemType
import com.devd.setting.data.SettingType
import com.devd.setting.dialog.AlarmTimSelectDialog
import kotlinx.coroutines.launch

@Composable
fun SettingScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
    onLockPage: (Boolean) -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.settingUiState.collectAsState()
    var isFontListSheet by remember { mutableStateOf(false) }
    var isDiaryLockSheet by remember { mutableStateOf(false) }

    val permissionHandler: IPermissionHandler = rememberPermissionHandler()
    val scope = rememberCoroutineScope()

    suspend fun checkPermission() {
        val grant = permissionHandler.requestPermissionIfNeeded(Consts.ALARM_PERMISSION)
        if (grant.any { !it.value }) {
            viewModel.showMessageDialog("푸쉬알림 권한이 필요합니다.")
        } else {
            viewModel.checkFcmToken()
        }
    }

    LaunchedEffect(isDiaryLockSheet) {
        onLockPage(isDiaryLockSheet)
    }

    BackHandler() {
        onBackClick()
    }

    SettingScreen(
        modifier = modifier,
        uiState = uiState,
        onItemClick = { type, value ->
            when (type) {
                SettingType.CLOUD_SYNC -> viewModel.syncDiaryData()
                SettingType.FONT_TYPE -> {
                    isFontListSheet = true
                }

                SettingType.ALERT_TIME -> scope.launch { checkPermission() }

                SettingType.DIARY_LOCK -> {
                    if (value == "1") viewModel.setLockPassword(lockPassword = null)
                    else isDiaryLockSheet = true
                }

                SettingType.DELETE_DATA -> viewModel.requestDeleteData()
                SettingType.APP_VERSION -> Unit
            }
        }
    )

    if (isFontListSheet) {
        OptionBottomSheet(
            title = "앱 폰트 선택",
            items = FontList.entries
                .mapIndexed { index, item ->
                    SheetItem(
                        id = index.toString(),
                        text = item.name,
                        isSelected = index == viewModel.savedSettingData.fontIndexInt
                    )
                },
            onItemSelected = {
                isFontListSheet = false
                viewModel.changeSettingData(SettingType.FONT_TYPE, it.id.toInt())
            },
            onDismissRequest = {
                isFontListSheet = false

            }
        )
    }

    if (uiState.alarmDialogInfo.isShow) {
        AlarmTimSelectDialog(
            uiState.alarmDialogInfo.selectHour ?: 0,
            uiState.alarmDialogInfo.selectMin ?: 0,
            {
                viewModel.dismissAlarmDialog()
            },
            { hour, min ->
                viewModel.registerScheduleUserPush("$hour$min")
            }
        )
    }

    if (isDiaryLockSheet) {
        LockDialog(
            modifier = modifier,
            type = LockType.REGISTER,
            inputFinish = { password ->
                viewModel.setLockPassword(password)
                isDiaryLockSheet = false
            },
            onDismissClick = {
                isDiaryLockSheet = false
            }
        )
    }

    uiState.messageDialog?.getMessage()?.ShowMessageDialog(
        leftButtonMessage = if (uiState.messageDialog?.messageType == "deleteDiary") R.string.cancel else null,
        onLeftButtonClick = when (uiState.messageDialog?.messageType) {
            "deleteDiary" -> {
                { viewModel.dismissMessageDialog() }
            }

            else -> null
        },
        onRightButtonClick = {
            when (uiState.messageDialog?.messageType) {
                "deleteDiary" -> {
                    viewModel.deleteAllDiaryData()
                }

                "completeDelete" -> {
                    val activity = (context as? Activity)
                    activity?.finish()
                }

                else -> viewModel.dismissMessageDialog()
            }
        }
    )

    uiState.isLoading.LoadingDialog()
}

@Preview
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    uiState: SettingUiState = SettingUiState(),
    onItemClick: (type: SettingType, itemValue: String?) -> Unit = { _, _ -> }
) {
    Column(
        modifier = modifier.then(Modifier.fillMaxSize())
    ) {
        Spacer(Modifier.height(20.dp))
        uiState.settingData.forEach { item ->
            when (item.settingType) {
                is ItemType.Action -> ActionItem(
                    key = item.type,
                    type = item.settingType,
                    onItemClick = {
                        onItemClick(item.type, null)
                    }
                )

                is ItemType.ActionValue -> ActionValueItem(
                    item.type,
                    item.settingType,
                    onItemClick = { onItemClick(item.type, item.settingType.value) })

                is ItemType.Value -> ValueItem(item.type, item.settingType)
                is ItemType.Switch -> SwitchItem(
                    key = item.type,
                    type = item.settingType,
                    onChangeSwitch = {
                        onItemClick(item.type, if (item.settingType.isOn) "1" else "0")
                    }

                )
            }

        }
    }
}

@Preview
@Composable
fun ActionItem(
    key: SettingType = SettingType.CLOUD_SYNC,
    type: ItemType.Action = ItemType.Action(true),
    onItemClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onItemClick)
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(key.strId),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = BlackColor
            )
        )
        if (type.isArrow) {
            Image(
                painter = painterResource(R.drawable.icon_angle_right),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun ActionValueItem(
    key: SettingType = SettingType.APP_VERSION,
    type: ItemType.ActionValue = ItemType.ActionValue(true, "2026-03-13"),
    onItemClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(key.strId),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = BlackColor
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            type.valueId?.let { stringResource(it) } ?: type.value?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = AccentColor
                    )
                )
            }
            Spacer(Modifier.width(10.dp))
            if (type.isArrow) {
                Image(
                    painter = painterResource(R.drawable.icon_angle_right),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
fun ValueItem(
    key: SettingType = SettingType.ALERT_TIME,
    type: ItemType.Value = ItemType.Value("2026-03-13")
) {
    val packInfo =
        LocalContext.current.packageManager.getPackageInfo(LocalContext.current.packageName, 0)

    val valueStr = when (key) {
        SettingType.APP_VERSION -> packInfo.versionName
        else -> type.valueId?.let { stringResource(it) } ?: type.value
    }

    Row(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(key.strId),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = BlackColor
            )
        )
        valueStr?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = AccentColor
                )
            )
        }
    }
}

@Preview
@Composable
fun SwitchItem(
    key: SettingType = SettingType.DIARY_LOCK,
    type: ItemType.Switch = ItemType.Switch(isOn = false),
    onChangeSwitch: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .fillMaxWidth()
            .clickable(onClick = onChangeSwitch),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(key.strId),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = BlackColor
            )
        )
        Switch(
            checked = type.isOn,

            colors = SwitchDefaults.colors().copy(
                checkedTrackColor = VioletColor,
                checkedThumbColor = WhiteColor,
                uncheckedTrackColor = BlackD9Color,
                uncheckedThumbColor = WhiteColor,
                checkedBorderColor = TransParents,
                uncheckedBorderColor = TransParents
            ),
            onCheckedChange = {
                onChangeSwitch()
            }
        )
    }
}