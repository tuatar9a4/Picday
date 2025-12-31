package com.devd.user.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(

) : ViewModel() {

    val nickName = mutableStateOf("")


    fun checkValidateId(id: String): Boolean {
        return true
    }

}