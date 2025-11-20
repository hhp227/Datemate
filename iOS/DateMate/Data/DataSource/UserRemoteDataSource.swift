//
//  UserRemoteDataSource.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/14.
//

import Foundation
import FirebaseAuth
import Combine

class UserRemoteDataSource {
    private let firebaseAuth: Auth
    
    private var authStateHandle: AuthStateDidChangeListenerHandle?
    
    private let userSubject = CurrentValueSubject<User?, Never>(nil)
    
    var userStatePublisher: AnyPublisher<User?, Never> {
        userSubject.eraseToAnyPublisher()
    }
    
    // Flow<FirebaseUser?> → AsyncStream<User?>
    /*var userStateStream: AsyncStream<User?> {
        AsyncStream { continuation in
            let handle = firebaseAuth.addStateDidChangeListener { _, user in
                continuation.yield(user)
            }
                
            continuation.onTermination = { _ in
                self.firebaseAuth.removeStateDidChangeListener(handle)
            }
        }
    }*/
    
    func signIn(email: String, password: String) -> AnyPublisher<Resource<User>, Never> {
        let subject = PassthroughSubject<Resource<User>, Never>()
            
        subject.send(.loading())
        firebaseAuth.signIn(withEmail: email, password: password) { result, error in
            if let user = result?.user {
                subject.send(.success(data: user))
            } else {
                subject.send(.error(message: error?.localizedDescription ?? "로그인 실패"))
            }
            subject.send(completion: .finished)
        }
        return subject.eraseToAnyPublisher()
    }
    
    func signOut() -> AnyPublisher<Resource<Bool>, Never> {
        let subject = PassthroughSubject<Resource<Bool>, Never>()
        
        subject.send(.loading())
        do {
            try firebaseAuth.signOut()
            subject.send(.success(data: true))
        } catch {
            subject.send(.error(message: error.localizedDescription))
        }
        subject.send(completion: .finished)
        return subject.eraseToAnyPublisher()
    }
    
    func signUp(email: String, password: String) -> AnyPublisher<AuthDataResult, Error> {
        Future { promise in
            self.firebaseAuth.createUser(withEmail: email, password: password) { authDataResult, error in
                if let auth = authDataResult {
                    promise(.success(auth))
                } else if let error = error {
                    promise(.failure(error))
                }
            }
        }.eraseToAnyPublisher()
    }
    
    /*func signIn(email: String, password: String) async throws -> AuthDataResult {
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
    }*/
    
    init(_ firebaseAuth: Auth) {
        self.firebaseAuth = firebaseAuth
        
        authStateHandle = firebaseAuth.addStateDidChangeListener { auth, user in
            self.userSubject.send(user)
        }
    }
    
    deinit {
        if let handle = authStateHandle {
            firebaseAuth.removeStateDidChangeListener(handle)
        }
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
