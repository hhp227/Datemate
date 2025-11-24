//
//  SignUpUiState.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

struct SignUpUiState {
    var name: String = ""
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var nameError: String? = nil
    var emailError: String? = nil
    var passwordError: String? = nil
    var confirmPasswordError: String? = nil
    var isLoading: Bool = false
    var isSignUpSuccess: Bool = false
    var errorMessage: String? = nil
    var isSignUpEnabled: Bool {
        return !name.isEmpty &&
            !email.isEmpty && emailError == nil &&
            !password.isEmpty && passwordError == nil &&
            !confirmPassword.isEmpty && confirmPassword == password
    }
}
