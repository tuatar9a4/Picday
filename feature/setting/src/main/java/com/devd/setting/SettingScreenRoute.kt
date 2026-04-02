package com.devd.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devd.commonsystem.R
import com.devd.commonsystem.theme.AccentColor
import com.devd.commonsystem.theme.BlackColor
import com.devd.commonsystem.theme.OneDayTypography
import com.devd.commonsystem.ui.Toolbar
import com.devd.setting.data.ItemType
import com.devd.setting.data.SettingType

@Composable
fun SettingScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.settingUiState.collectAsState()

    SettingScreen(
        modifier = modifier,
        uiState = uiState,
        onItemClick = {
            when(it){
                SettingType.CLOUD_SYNC -> viewModel.syncDiaryData()
                SettingType.FONT_TYPE -> TODO()
                SettingType.ALERT_TIME -> TODO()
                SettingType.MONTH_TYPE -> TODO()
                SettingType.APP_VERSION -> TODO()
            }
        }
    )

}

@Preview
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    uiState : SettingUiState = SettingUiState(),
    onItemClick: (type: SettingType) -> Unit = {}
) {
    Column(
        modifier = modifier.then(Modifier.fillMaxSize())
    ) {
        Toolbar(
            titleBox = {
                Text(
                    text = ""
                )
            },
            leftButtons = {
                Image(
                    painter = painterResource(R.drawable.icon_back_arrow),
                    contentDescription = null
                )
            }
        )
        uiState.settingData.forEach { item ->
            when (item.settingType) {
                is ItemType.Action -> ActionItem(item.type, item.settingType)
                is ItemType.ActionValue -> ActionValueItem(
                    item.type,
                    item.settingType,
                    onItemClick = { onItemClick(item.type) })

                is ItemType.Value -> ValueItem(item.type, item.settingType)
            }

        }
    }
}

@Preview
@Composable
fun ActionItem(
    key: SettingType = SettingType.CLOUD_SYNC,
    type: ItemType.Action = ItemType.Action(true)
) {
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(key.strId),
            style = OneDayTypography.bodyMedium.copy(
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
    key: SettingType = SettingType.MONTH_TYPE,
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
            style = OneDayTypography.bodyMedium.copy(
                color = BlackColor
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            type.valueId?.let { stringResource(it) } ?: type.value?.let {
                Text(
                    text = it,
                    style = OneDayTypography.labelLarge.copy(
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
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(key.strId),
            style = OneDayTypography.bodyMedium.copy(
                color = BlackColor
            )
        )
        type.valueId?.let { stringResource(it) } ?: type.value?.let {
            Text(
                text = it,
                style = OneDayTypography.labelLarge.copy(
                    color = AccentColor
                )
            )
        }
    }
}

