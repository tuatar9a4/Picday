package com.devd.model.remote

import com.devd.model.local.UserInfo
import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("email")
    val email: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("createdAt")
    val createdAt: Long
){
    fun toUserInfo() = UserInfo(
        email = email,
        nickname = nickname,
        uuid = uuid
    )
}