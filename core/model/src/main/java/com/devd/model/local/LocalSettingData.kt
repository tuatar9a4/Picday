package com.devd.model.local


data class LocalSettingData(
    val syncTime: String = "0",
    val fontIndex: String = "0",
) {
    val syncTimeLong: Long get() = syncTime.toLongOrNull() ?: 0L
    val fontIndexInt: Int get() = fontIndex.toIntOrNull() ?: 0
}
