//
//  ForgotPasswordViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import Foundation

class ForgotPasswordViewModel: ObservableObject {
    private let userRepository: UserRepository
    
    @Published var uiState = ForgotPasswordUiState()
    
    func onEmailChange(newValue: String) {
        uiState.email = newValue
    }
    
    func sendResetEmail() {
    }
    
    func consumeMessage() {
    }
    
    init(_ userRepository: UserRepository) {
        self.userRepository = userRepository
    }
}
