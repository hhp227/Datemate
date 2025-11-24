package com.hhp227.datemate.ui.auth.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel internal constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(newValue: String) {
        _uiState.update { it.copy(name = newValue, nameError = null) }
    }

    fun onEmailChange(newValue: String) {
        _uiState.update { it.copy(email = newValue, emailError = null) }
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update { it.copy(password = newValue, passwordError = null) }
    }

    fun onConfirmPasswordChange(newValue: String) {
        _uiState.update { it.copy(confirmPassword = newValue, confirmPasswordError = null) }
    }

    fun signUp() {
        val currentState = _uiState.value

        // 간단한 유효성 검사
        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(nameError = "이름을 입력해주세요.") }
            return
        }
        if (currentState.email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.update { it.copy(emailError = "유효한 이메일을 입력해주세요.") }
            return
        }
        if (currentState.password.length < 6) {
            _uiState.update { it.copy(passwordError = "비밀번호는 6자 이상이어야 합니다.") }
            return
        }
        if (currentState.password != currentState.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "비밀번호가 일치하지 않습니다.") }
            return
        }

        viewModelScope.launch {
            /*userRepository.getSignUpResultStream(currentState.email, currentState.password)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                        }
                        is Resource.Success -> {
                            // 성공 시 Success 플래그 true
                            _uiState.update { it.copy(isLoading = false, isSignUpSuccess = true) }
                        }
                        is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                        }
                    }
                }*/
            _uiState.update { it.copy(isLoading = false, isSignUpSuccess = true) }
        }
    }

    fun consumeSuccessEvent() {
        _uiState.update { it.copy(isSignUpSuccess = false) }
    }
}