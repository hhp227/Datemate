package com.hhp227.datemate.ui.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.model.UserCache
import com.hhp227.datemate.data.repository.ProfileRepository
import com.hhp227.datemate.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SignInViewModel internal constructor(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState

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

    private suspend fun fetchUserProfile(userId: String) {
        val profile = profileRepository.getProfile(userId)
        val userCache = UserCache(userId)

        if (profile != null) {
            userRepository.storeUserProfile(userCache)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    message = null
                )
            }
        }
    }

    fun onEmailChanged(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                emailError = validateEmail(value)
            )
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordError = validatePassword(value)
            )
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            userRepository.getSignInResultStream(email, password)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Error<*> -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    message = result.message ?: "알 수 없는 로그인 오류가 발생했습니다."
                                )
                            }
                        }
                        is Resource.Loading<*> -> _uiState.update { it.copy(isLoading = true, message = null) }
                        is Resource.Success<*> -> {
                            val firebaseUser = result.data

                            if (firebaseUser == null) {
                                _uiState.update { it.copy(isLoading = false, message = "로그인 성공 후 사용자 정보를 찾을 수 없습니다.") }
                                return@collectLatest
                            }
                            fetchUserProfile(firebaseUser.uid)
                        }
                    }
                }
        }
    }

    init {
        viewModelScope.launch {
            userRepository.remoteUserStateFlow
                .filterNotNull()
                .map { profileRepository.getProfile(it.uid) }
                .collectLatest { profile ->
                    _uiState.update { it.copy(isAlreadySignIn = profile == null) }
                }
        }
    }

    companion object {
        private const val EMAIL_VALIDATION_REGEX = "^(.+)@(.+)\$"
    }
}