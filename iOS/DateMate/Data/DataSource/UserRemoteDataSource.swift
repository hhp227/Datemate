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
    
    private let userSubject = CurrentValueSubject<FirebaseAuth.User?, Never>(nil)
    
    var userStatePublisher: AnyPublisher<FirebaseAuth.User?, Never> {
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
    
    func signIn(email: String, password: String) -> AnyPublisher<Resource<FirebaseAuth.User>, Never> {
        Future { promise in
            self.firebaseAuth.signIn(withEmail: email, password: password) { result, error in
                if let user = result?.user {
                    promise(.success(.success(data: user)))
                } else {
                    promise(.success(.error(message: error?.localizedDescription ?? "로그인 실패")))
                }
            }
        }
        .prepend(.loading())
        .eraseToAnyPublisher()
    }
    
    func signOut() -> AnyPublisher<Resource<Bool>, Never> {
        Future { promise in
            do {
                try self.firebaseAuth.signOut()
                promise(.success(.success(data: true)))
            } catch {
                promise(.success(.error(message: error.localizedDescription)))
            }
        }
        .prepend(.loading())
        .eraseToAnyPublisher()
    }
    
    func signUp(email: String, password: String) -> AnyPublisher<Resource<FirebaseAuth.User>, Error> {
        Future { promise in
            self.firebaseAuth.createUser(withEmail: email, password: password) { result, error in
                if let user = result?.user {
                    promise(.success(.success(data: user)))
                } else if let error = error {
                    promise(.failure(error))
                }
            }
        }
        .prepend(.loading())
        .eraseToAnyPublisher()
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
    
    func sendPasswordResetEmail(email: String) -> AnyPublisher<Resource<Bool>, Never> {
        return Future { promise in
            self.firebaseAuth.sendPasswordReset(withEmail: email) { error in
                if let error = error {
                    promise(.success(.error(message: "비밀번호 재설정 이메일 전송 실패: \(error.localizedDescription)")))
                } else {
                    promise(.success(.success(data: true)))
                }
            }
        }
        .prepend(.loading())
        .eraseToAnyPublisher()
    }
    
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
