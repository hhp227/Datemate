package com.hhp227.datemate.ui.auth.signin

data class SignInUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val message: String? = null
) {
    val isSubmitEnabled: Boolean
        get() {
            val isEmailValid = email.isNotBlank() && emailError == null
            val isPasswordValid = password.isNotBlank() && passwordError == null
            return isEmailValid && isPasswordValid
        }
}