package com.devd.model.remote

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)
