package com.devd.commonsystem.utils

object StringRexFormat{
    const val ID_REGEX = "^[a-zA-Z0-9]{2,10}$"
    const val ID_WORD_REGEX = "^[a-zA-Z0-9]+$"
    const val PASSWORD_WORD_REGEX = "^[A-Za-z\\d!@#$%^&*]+$"
    const val PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,20}$"
    const val NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{0,10}$"
}

fun String.checkValidateRex(regexFormat: String): Boolean {
    return Regex(regexFormat).matches(this)
}