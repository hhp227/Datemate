//
//  SignInUiState.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/18.
//

import Foundation

struct SignInUiState {
    var email: String = ""
    var emailError: String? = nil
    var password: String = ""
    var passwordError: String? = nil
    var isLoading: Bool = false
    var message: String? = nil
    var isSubmitEnabled: Bool {
        let isEmailValid = !email.isEmpty && emailError == nil
        let isPasswordValid = !password.isEmpty && passwordError == nil
        return isEmailValid && isPasswordValid
    }
}
