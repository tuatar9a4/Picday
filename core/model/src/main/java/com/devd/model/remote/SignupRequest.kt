package com.devd.model.remote

data class SignupRequest(
    val email: String,
    val password: String,
    val uuid: String,
    val nickname: String,
)