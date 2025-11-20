package com.hhp227.datemate.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.data.repository.UserRepository
import com.hhp227.datemate.common.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SignInViewModel internal constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState

    private fun canSubmit(email: String, password: String): Boolean {
        return !email.isEmpty() && validateEmail(email) == null && !password.isEmpty() && validatePassword(password) == null
    }

    private fun isEmailValid(email: String): Boolean {
        return Pattern.matches(EMAIL_VALIDATION_REGEX, email)
    }

    private fun validateEmail(value: String): String? {
        return if (value.isEmpty() || isEmailValid(value)) null else "Invalid email: $value"
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 3
    }

    private fun validatePassword(value: String): String? {
        return if (value.isEmpty() || isPasswordValid(value)) null else "Invalid password"
    }

    fun onEmailChanged(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                emailError = validateEmail(value),
                isSignInEnabled = canSubmit(value, it.password)
            )
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordError = validatePassword(value),
                isSignInEnabled = canSubmit(it.email, value)
            )
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            userRepository.getSignInResultStream(email, password)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Error<*> -> Unit
                        is Resource.Loading<*> -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success<*> -> Unit
                    }
                }
        }
    }

    companion object {
        private const val EMAIL_VALIDATION_REGEX = "^(.+)@(.+)\$"
    }
}