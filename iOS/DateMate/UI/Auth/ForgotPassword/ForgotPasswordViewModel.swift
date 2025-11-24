//
//  ForgotPasswordViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import Foundation

class ForgotPasswordViewModel: ObservableObject {
    private let userRepository: UserRepository
    
    init(_ userRepository: UserRepository) {
        self.userRepository = userRepository
    }
}
