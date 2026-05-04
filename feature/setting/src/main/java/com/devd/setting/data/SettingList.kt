package com.devd.setting.data

val settingList = listOf(
//    SettingItem(
//        type = SettingType.CLOUD_SYNC,
//        settingType = ItemType.ActionValue(isArrow = true, value = "2026-03-13")
//    ),
    SettingItem(
        type = SettingType.FONT_TYPE,
        settingType = ItemType.ActionValue(isArrow = true, value = "고딕")
    ),
    SettingItem(
        type = SettingType.ALERT_TIME,
        settingType = ItemType.ActionValue(isArrow = true, value = "시간")
    ),
    SettingItem(
        type = SettingType.DIARY_LOCK,
        settingType = ItemType.Switch(isOn = true)
    ),
    SettingItem(
        type = SettingType.DELETE_DATA,
        settingType = ItemType.Action(true)
    ),
    SettingItem(
        type = SettingType.APP_VERSION,
        settingType = ItemType.Value("")
    )
)