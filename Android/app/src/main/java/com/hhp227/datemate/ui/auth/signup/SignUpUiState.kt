package com.hhp227.datemate.ui.auth.signup

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isSignUpSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isSignUpEnabled: Boolean
        get() = email.isNotBlank() && emailError == null &&
                password.isNotBlank() && passwordError == null &&
                confirmPassword.isNotBlank() && confirmPassword == password
}