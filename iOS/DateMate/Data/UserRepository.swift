//
//  UserRepository.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/01.
//

import Foundation
import FirebaseAuth
import Combine

class UserRepository {
    private let userRemoteDataSource: UserRemoteDataSource
    
    private var authStateHandle: AuthStateDidChangeListenerHandle?
    
    private let userSubject = CurrentValueSubject<User?, Never>(nil)
    
    var userStatePublisher: AnyPublisher<User?, Never> {
        userSubject.eraseToAnyPublisher()
    }
    
    // Flow<FirebaseUser?> → AsyncStream<User?>
    /*var userStateStream: AsyncStream<User?> {
        AsyncStream { continuation in
            let handle = userRemoteDataSource.addAuthStateListener { user in
                continuation.yield(user)
            }
                
            continuation.onTermination = { _ in
                self.userRemoteDataSource.removeAuthStateListener(handle)
            }
        }
    }*/
    
    init(_ userRemoteDataSource: UserRemoteDataSource) {
        self.userRemoteDataSource = userRemoteDataSource
        
        authStateHandle = userRemoteDataSource.addAuthStateListener { auth, user in
            self.userSubject.send(user)
        }
    }
    
    deinit {
        if let handle = authStateHandle {
            userRemoteDataSource.removeAuthStateListener(handle)
        }
    }
    
    private static var instance: UserRepository? = nil
    
    static func getInstance(userRemoteDataSource: UserRemoteDataSource) -> UserRepository {
        if let instance = self.instance {
            return instance
        } else {
            let userRepository = UserRepository(userRemoteDataSource)
            self.instance = userRepository
            return userRepository
        }
    }
}

/**
 사용 예시
 let dataSource = UserRemoteDataSource.getInstance()
 let repository = UserRepository.getInstance(userRemoteDataSource: dataSource)

 let cancellable = repository.userStatePublisher
     .sink { user in
         if let user = user {
             print("로그인된 사용자: \(user.email ?? "")")
         } else {
             print("로그아웃 상태")
         }
     }

 Task {
     do {
         let result = try await dataSource.signIn(email: "test@example.com", password: "123456")
         print("로그인 성공: \(result.user.email ?? "")")
     } catch {
         print("로그인 실패: \(error.localizedDescription)")
     }
 }
 */
