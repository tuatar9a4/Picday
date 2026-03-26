package com.devd.user.register.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devd.commonsystem.R
import com.devd.data.repository.UserRepository
import com.devd.datastore.DataStoreKey
import com.devd.datastore.DataStoreRepository
import com.devd.model.local.UserInfo
import com.devd.user.register.data.MessageInfo
import com.devd.user.register.data.MessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val messageInfo: MessageInfo? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    private val _userResult = MutableSharedFlow<UserInfo>()
    val userResult = _userResult.asSharedFlow()


    fun requestLogin(id: String, pw: String) {
        viewModelScope.launch {
            _loginUiState.update { it.copy(isLoading = true) }

            userRepository.requestLoginUser(id, pw)?.let { userInfo ->
                dataStoreRepository.setPreferData(DataStoreKey.UserToken, userInfo.accessToken)
                dataStoreRepository.setPreferData(DataStoreKey.UserReToken, userInfo.refreshToken)
                _userResult.emit(dataStoreRepository.setUserInfo(userInfo.toUserInfo()))
            } ?: run { // FailLogin
                _loginUiState.update {
                    it.copy(
                        isLoading = false,
                        messageInfo = MessageInfo(
                            type = MessageType.LOGIN_FAIL,
                            messageId = R.string.fail_login_message
                        )
                    )
                }
            }
        }
    }

    fun dismissMessageDialog() {
        _loginUiState.update { it.copy(messageInfo = null) }
    }

}