package com.hhp227.datemate

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SignInViewModel(private val repository: UserRepository) : ViewModel() {
    var signInResult by mutableStateOf(SignInResult())

    private fun isEmailValid(email: String): Boolean {
        return if (email.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 3
    }

    fun signIn(email: String, password: String) {
        if (isEmailValid(email) && isPasswordValid(password)) {
            //TODO 더 좋은 방법있으면 개선해나가기
            repository.signIn(email, password) {
                when (it) {
                    SignInStatus.Success -> {
                        signInResult = SignInResult(true)
                    }
                    SignInStatus.Failure -> {
                        signInResult = SignInResult(false)
                    }
                    SignInStatus.Loading -> {
                        Log.e("TEST", "loading중입니다.")
                    }
                }
            }
        }
    }

    data class SignInResult(val success: Boolean = false)
}

@Suppress("UNCHECKED_CAST")
class SignInViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
