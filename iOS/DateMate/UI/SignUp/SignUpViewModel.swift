//
//  RegisterViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2021/11/28.
//

import Foundation
import Combine
import FirebaseAuth

class SignUpViewModel: ObservableObject {
    private let repository: UserRepository
    
    private var subscription = Set<AnyCancellable>()
    
    func signUp(email: String, password: String) {
        
    }
    
    init(_ repository: UserRepository) {
        self.repository = repository
    }
    
    deinit {
        subscription.removeAll()
    }
}
