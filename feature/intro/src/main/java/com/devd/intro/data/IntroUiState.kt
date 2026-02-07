package com.devd.intro.data

sealed class IntroUiState

data class Loading(val isShow: Boolean) : IntroUiState()
data object MoveToHome : IntroUiState()
