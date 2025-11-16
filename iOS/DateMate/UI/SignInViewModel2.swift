//
//  SignInViewModel2.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/16.
//

import Foundation

class SignInViewModel2: ObservableObject {
    private let userRepository: UserRepository
    
    init(_ userRepository: UserRepository) {
        self.userRepository = userRepository
    }
}
