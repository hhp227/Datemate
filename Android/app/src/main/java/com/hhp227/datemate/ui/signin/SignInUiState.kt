package com.hhp227.datemate.ui.signin

data class SignInUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isSignInEnabled: Boolean = false,
)