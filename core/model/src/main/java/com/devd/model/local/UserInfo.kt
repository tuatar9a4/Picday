package com.devd.model.local

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    @ColumnInfo("email") val email: String,
    @ColumnInfo("nickname") val nickname: String,
    @ColumnInfo("uuid") val uuid: String
)