package com.hhp227.datemate.ui.auth.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
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

    private suspend fun createUserProfile(userId: String, email: String?) {
        userRepository.createUserProfileResultStream(userId, email).collect { dataResource ->
            when (dataResource) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSignUpSuccess = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "초기 데이터 저장 실패: ${dataResource.message}") }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
            }
        }
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

    fun signUp(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "유효한 이메일을 입력해주세요.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(passwordError = "비밀번호는 6자 이상이어야 합니다.") }
            return
        }
        if (password != confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "비밀번호가 일치하지 않습니다.") }
            return
        }

        viewModelScope.launch {
            userRepository.getSignUpResultStream(email, password)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                        }
                        is Resource.Success -> {
                            resource.data?.also { firebaseUser ->
                                createUserProfile(firebaseUser.uid, firebaseUser.email)
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                        }
                    }
                }
            _uiState.update { it.copy(isLoading = false, isSignUpSuccess = true) }
        }
    }

    fun consumeSuccessEvent() {
        _uiState.update { it.copy(isSignUpSuccess = false) }
    }
}