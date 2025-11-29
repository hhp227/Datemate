package com.hhp227.datemate.ui.auth.forgotpassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(newValue: String) {
        _uiState.update { it.copy(email = newValue, emailError = null, message = null) }
    }

    fun sendResetEmail(email: String) {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "유효한 이메일 주소를 입력해주세요.") }
            return
        }
        viewModelScope.launch {
            userRepository.getPasswordResetResultStream(email)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isEmailSent = true,
                                    message = "비밀번호 재설정 이메일이 ${email}로 전송되었습니다."
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isEmailSent = false,
                                    message = "오류: ${resource.message}. 다시 시도해주세요."
                                )
                            }
                        }
                    }
                }
        }
    }

    fun consumeMessage() {
        _uiState.update { it.copy(isEmailSent = false, message = null) }
    }
}