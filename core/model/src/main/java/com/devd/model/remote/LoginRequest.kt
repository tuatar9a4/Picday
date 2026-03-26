package com.devd.model.remote

import androidx.room.ColumnInfo
import com.devd.model.local.UserInfo
import kotlinx.serialization.Serializable

data class LoginRequest(
    @ColumnInfo("email") val email: String,
    @ColumnInfo("password") val password: String
)

@Serializable
data class LoginResponse(
    @ColumnInfo("accessToken") val accessToken: String,
    @ColumnInfo("refreshToken") val refreshToken: String,
    @ColumnInfo("userId") val userId: Long,
    @ColumnInfo("email") val email: String,
    @ColumnInfo("nickname") val nickname: String,
    @ColumnInfo("uuid") val uuid: String
){
    fun toUserInfo() = UserInfo(
        email = email,
        nickname = nickname,
        uuid = uuid
    )
}