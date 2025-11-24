package com.hhp227.datemate.ui.auth.signup

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isSignUpSuccess: Boolean = false, // 회원가입 성공 플래그
    val errorMessage: String? = null
) {
    val isSignUpEnabled: Boolean
        get() = name.isNotBlank() &&
                email.isNotBlank() && emailError == null &&
                password.isNotBlank() && passwordError == null &&
                confirmPassword.isNotBlank() && confirmPassword == password
}