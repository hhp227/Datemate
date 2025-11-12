package com.hhp227.datemate.ui.legacy

import java.util.regex.Pattern

class EmailState : TextFieldState(validator = ::isEmailValid, errorFor = ::emailValidationError)

private const val EMAIL_VALIDATION_REGEX = "^(.+)@(.+)\$"

private fun emailValidationError(email: String): String {
    return "Invalid email: $email"
}

private fun isEmailValid(email: String): Boolean {
    return Pattern.matches(EMAIL_VALIDATION_REGEX, email)
}