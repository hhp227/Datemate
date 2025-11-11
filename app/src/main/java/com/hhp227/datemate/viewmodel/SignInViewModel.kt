package com.hhp227.datemate.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.hhp227.datemate.data.UserRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SignInViewModel(private val repository: UserRepository) : ViewModel() {
    var signInResult by mutableStateOf(SignInResult(repository.getCurrentUser() != null))

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

    private fun onReceive(batch: AuthResult?) {
        if (batch != null) {
            this.signInResult = SignInResult(true)
        } else {
            this.signInResult = SignInResult(false)
        }
    }

    fun signIn(email: String, password: String) {
        if (isEmailValid(email) && isPasswordValid(password)) {
            repository.signIn(email, password).onEach(::onReceive).launchIn(viewModelScope)
        }
    }

    fun signOut() {
        repository.signOut()
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
