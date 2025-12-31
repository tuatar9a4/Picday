package com.devd.commonsystem.utils

fun String.checkValidateRex(regexFormat: String): Boolean {
    return Regex(regexFormat).matches(this)
}