package com.hhp227.datemate.ui.auth.phoneauth

data class PhoneAuthUiState(
    val isLoading: Boolean = false,
    val isCodeSent: Boolean = false,
    val isVerified: Boolean = false,
    val errorMessage: String? = null
)