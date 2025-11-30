//
//  PhoneAuthUiState.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/30.
//

import Foundation

struct PhoneAuthUiState {
    var isLoading: Bool = false
    var isCodeSent: Bool = false
    var isVerified: Bool = false
    var errorMessage: String? = nil
}
