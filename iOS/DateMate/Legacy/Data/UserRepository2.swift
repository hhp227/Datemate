//
//  UserRepository.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/14.
//

import Foundation
import FirebaseAuth
import Combine

class UserRepository2 {
    let auth = Auth.auth()
    
    var user: User?
    
    func signIn(email: String, password: String) -> AnyPublisher<AuthDataResult, Error> {
        Future { promise in
            self.auth.signIn(withEmail: email, password: password) { authDataResult, error in
                if let auth = authDataResult {
                    promise(.success(auth))
                } else if let error = error {
                    promise(.failure(error))
                }
            }
        }.eraseToAnyPublisher()
    }
    
    func signOut() {
        try? auth.signOut()
    }
    
    func signUp(email: String, password: String) -> AnyPublisher<AuthDataResult, Error> {
        Future { promise in
            self.auth.createUser(withEmail: email, password: password) { authDataResult, error in
                if let auth = authDataResult {
                    promise(.success(auth))
                } else if let error = error {
                    promise(.failure(error))
                }
            }
        }.eraseToAnyPublisher()
    }
    
    func getCurrentUser() -> User? {
        return auth.currentUser
    }
}
