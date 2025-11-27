//
//  ForgotPasswordUiState.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/27.
//

import Foundation

struct ForgotPasswordUiState {
    var email: String = ""
    var emailError: String = ""
    var isLoading: Bool = false
    var isEmailSent: Bool = false
    var message: String? = nil
}
