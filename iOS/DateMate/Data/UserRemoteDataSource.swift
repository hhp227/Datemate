//
//  UserRemoteDataSource.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/14.
//

import Foundation
import FirebaseAuth

class UserRemoteDataSource {
    private let firebaseAuth: Auth
    
    func signIn(email: String, password: String) async throws -> AuthDataResult {
        return try await withCheckedThrowingContinuation { continuation in
            firebaseAuth.signIn(withEmail: email, password: password) { authResult, error in
                if let error = error {
                    continuation.resume(throwing: error)
                } else if let authResult = authResult {
                    continuation.resume(returning: authResult)
                }
            }
        }
    }
    
    func signUp(email: String, password: String) async throws -> AuthDataResult {
        return try await withCheckedThrowingContinuation { continuation in
            firebaseAuth.createUser(withEmail: email, password: password) { authResult, error in
                if let error = error {
                    continuation.resume(throwing: error)
                } else if let authResult = authResult {
                    continuation.resume(returning: authResult)
                }
            }
        }
    }

    func addAuthStateListener(_ listener: @escaping (Auth?, User?) -> Void) -> AuthStateDidChangeListenerHandle {
        firebaseAuth.addStateDidChangeListener(listener)
    }

    func removeAuthStateListener(_ handle: AuthStateDidChangeListenerHandle) {
        firebaseAuth.removeStateDidChangeListener(handle)
    }
    
    init(_ firebaseAuth: Auth) {
        self.firebaseAuth = firebaseAuth
    }
    
    private static var instance: UserRemoteDataSource? = nil
    
    static func getInstance(auth: Auth) -> UserRemoteDataSource {
        if let instance = self.instance {
            return instance
        } else {
            let userDataSource = UserRemoteDataSource(auth)
            self.instance = userDataSource
            return userDataSource
        }
    }
}
