package com.hhp227.datemate.ui.signup

import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 3
    }

    private fun passwordAndConfirmationValid(password: String, confirmedPassword: String): Boolean {
        return isPasswordValid(password) && password == confirmedPassword
    }
}