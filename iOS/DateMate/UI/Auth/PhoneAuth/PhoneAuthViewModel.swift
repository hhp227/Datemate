//
//  PhoneAuthViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/30.
//

import Foundation
import Combine

class PhoneAuthViewModel: ObservableObject {
    let userRepository: UserRepository
    
    init(_ userRepository: UserRepository) {
        self.userRepository = userRepository
    }
}
