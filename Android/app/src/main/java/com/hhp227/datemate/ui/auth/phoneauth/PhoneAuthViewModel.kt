package com.hhp227.datemate.ui.auth.phoneauth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhoneAuthViewModel internal constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var verificationId: String? = null
        private set

    private val _uiState = MutableStateFlow(PhoneAuthUiState())
    val uiState: StateFlow<PhoneAuthUiState> = _uiState.asStateFlow()

    fun sendOtp(phoneNumber: String, activityProvider: () -> Activity) {
        viewModelScope.launch {
            userRepository.sendOtp(phoneNumber, activityProvider)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                        }
                        is Resource.Success -> {
                            verificationId = resource.data

                            _uiState.update { it.copy(isLoading = false, isCodeSent = true) }
                        }
                        is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                        }
                    }
                }
        }
    }

    fun verifyOtp(code: String) {
        val id = verificationId ?: return

        viewModelScope.launch {
            userRepository.verifyOtp(id, code)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                        }
                        is Resource.Success -> {
                            _uiState.update { it.copy(isLoading = false, isVerified = true) }
                        }
                        is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                        }
                    }
                }
        }
    }
}