package com.devd.setting.data

import com.devd.commonsystem.R

enum class SettingType(var strId: Int) {
    CLOUD_SYNC(R.string.setting_cloud_sync),
    FONT_TYPE(R.string.setting_font_type),
    ALERT_TIME(R.string.setting_alert_time),
    DIARY_LOCK(R.string.setting_lock_diary),
    APP_VERSION(R.string.setting_app_version)
}

sealed interface ItemType {
    data class Action(var isArrow: Boolean = false) : ItemType
    data class ActionValue(
        var isArrow: Boolean = false,
        val value: String? = null,
        val valueId: Int? = null
    ) : ItemType

    data class Switch(
        val value: String? = null,
        val isOn: Boolean = false
    ) : ItemType

    data class Value(
        val value: String? = null,
        val valueId: Int? = null
    ) : ItemType
}

data class SettingItem(
    val type: SettingType,
    val settingType: ItemType,
)
