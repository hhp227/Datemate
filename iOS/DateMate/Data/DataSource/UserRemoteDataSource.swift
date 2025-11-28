//
//  UserRemoteDataSource.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/14.
//

import Foundation
import FirebaseAuth
import FirebaseFirestore
import Combine

class UserRemoteDataSource {
    private let firebaseAuth: Auth
    
    private let firestore: Firestore
    
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
    
    func signIn(email: String, password: String) -> AnyPublisher<FirebaseAuth.User, Error> {
        Future { promise in
            self.firebaseAuth.signIn(withEmail: email, password: password) { result, error in
                if let user = result?.user {
                    promise(.success(user))
                } else {
                    promise(.failure(error ?? NSError(domain: "SignInError", code: -1)))
                }
            }
        }
        .eraseToAnyPublisher()
    }
    
    func signOut() -> AnyPublisher<Bool, Never> {
        Future { promise in
            do {
                try self.firebaseAuth.signOut()
                promise(.success(true))
            } catch {
                promise(.success(false))
            }
        }
        .eraseToAnyPublisher()
    }
    
    func signUp(email: String, password: String) -> AnyPublisher<FirebaseAuth.User, Error> {
        Future { promise in
            self.firebaseAuth.createUser(withEmail: email, password: password) { result, error in
                if let user = result?.user {
                    promise(.success(user))
                } else if let error = error {
                    promise(.failure(error))
                }
            }
        }
        .eraseToAnyPublisher()
    }
    
    func updateUserProfile(
        userId: String,
        fullName: String,
        gender: String,
        birthdayMillis: Int64,
        bio: String,
        job: String,
        profileImageUrls: [String]?
    ) -> AnyPublisher<Bool, Error> {
        Future { promise in
            guard let user = self.firebaseAuth.currentUser else {
                promise(.failure(NSError(domain: "NoUserError", code: -1)))
                return
            }
            let changeRequest = user.createProfileChangeRequest()
            changeRequest.displayName = fullName
            
            if let firstUrl = profileImageUrls?.first, let url = URL(string: firstUrl) {
                changeRequest.photoURL = url
            }
            changeRequest.commitChanges { error in
                if let error = error {
                    promise(.failure(error))
                    return
                }
                let userDocument = self.firestore.collection("users").document(userId)
                let userData: [String: Any] = [
                    "userId": userId,
                    "fullName": fullName,
                    "gender": gender,
                    "birthdayMillis": birthdayMillis,
                    "bio": bio,
                    "job": job,
                    "profileImageUrls": profileImageUrls ?? [],
                    "updatedAt": FieldValue.serverTimestamp()
                ]
                
                userDocument.setData(userData, merge: true) { error in
                    if let error = error {
                        promise(.failure(error))
                    } else {
                        promise(.success(true))
                    }
                }
            }
        }
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
    
    func sendPasswordResetEmail(email: String) -> AnyPublisher<Bool, Error> {
        return Future { promise in
            self.firebaseAuth.sendPasswordReset(withEmail: email) { error in
                if let error = error {
                    promise(.failure(error))
                } else {
                    promise(.success(true))
                }
            }
        }
        .eraseToAnyPublisher()
    }
    
    init(_ firebaseAuth: Auth, _ firestore: Firestore) {
        self.firebaseAuth = firebaseAuth
        self.firestore = firestore
        
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
    
    static func getInstance(auth: Auth, firestore: Firestore) -> UserRemoteDataSource {
        if let instance = self.instance {
            return instance
        } else {
            let userDataSource = UserRemoteDataSource(auth, firestore)
            self.instance = userDataSource
            return userDataSource
        }
    }
}
