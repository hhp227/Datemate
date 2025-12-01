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
    
    func sendPasswordResetEmail(email: String) -> AnyPublisher<Bool, Error> {
        Future { promise in
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
    
    func fetchUserProfile(userId: String) -> AnyPublisher<Profile?, Error> {
        Future<Profile?, Error> { promise in
            self.firestore.collection("profiles").document(userId).getDocument { snapshot, error in
                if let error = error {
                    return promise(.failure(error))
                }
                if let data = try? snapshot?.data(as: Profile.self) {
                    return promise(.success(data))
                }
                promise(.success(nil))
            }
        }.eraseToAnyPublisher()
    }
    
    func createUserProfile(userId: String, email: String?) -> AnyPublisher<Bool, Error> {
        Future<Bool, Error> { promise in
            let data: [String: Any] = [
                "email": email ?? "",
                "phoneNumber": "",
                "createdAt": FieldValue.serverTimestamp(),
                "lastLogin": FieldValue.serverTimestamp(),
                "status": "active"
            ]
            
            self.firestore.collection("users").document(userId)
                .setData(data, merge: true) { error in
                    if let error = error {
                        return promise(.failure(error))
                    }
                    promise(.success(true))
                }
        }
        .eraseToAnyPublisher()
    }
    
    func updateUserProfile(
        userId: String,
        name: String,
        gender: String,
        birthdayMillis: Int64,
        bio: String,
        job: String,
        profileImageUrls: [String]?
    ) -> AnyPublisher<String, Error> {
        userStatePublisher
            .compactMap { $0 }
            .flatMap { user -> AnyPublisher<String, Error> in
                let profileUpdates = user.createProfileChangeRequest()
                profileUpdates.displayName = name
                profileUpdates.photoURL = URL(string: profileImageUrls?.first ?? "")
                let userDoc = self.firestore.collection("profiles").document(userId)
                let userData: [String: Any] = [
                    "name": name,
                    "gender": gender,
                    "birthday": Timestamp(date: Date(timeIntervalSince1970: TimeInterval(birthdayMillis / 1000))),
                    "bio": bio,
                    "job": job,
                    "photos": profileImageUrls ?? [],
                    "updatedAt": FieldValue.serverTimestamp()
                ]
                return Future<String, Error> { promise in
                    profileUpdates.commitChanges { err in
                        if let err = err {
                            return promise(.failure(err))
                        }
                        userDoc.setData(userData, merge: true) { err2 in
                            if let err2 = err2 {
                                return promise(.failure(err2))
                            }
                            promise(.success(userId))
                        }
                    }
                }
                .eraseToAnyPublisher()
            }
            .eraseToAnyPublisher()
    }
    
    func sendOtp(phoneNumber: String) -> AnyPublisher<String, Error> {
        Future<String, Error> { promise in
            PhoneAuthProvider.provider().verifyPhoneNumber(phoneNumber, uiDelegate: nil) { verificationId, error in
                if let error = error {
                    return promise(.failure(error))
                }
                guard let id = verificationId else {
                    return promise(.failure(NSError(domain: "OTP Error", code: -1)))
                }
                promise(.success(id))
            }
        }
        .eraseToAnyPublisher()
    }
    
    func verifyOtp(verificationId: String, code: String) -> AnyPublisher<Void, Error> {
        userStatePublisher
            .compactMap { $0 }
            .flatMap { user -> AnyPublisher<Void, Error> in
                let credential = PhoneAuthProvider.provider()
                    .credential(withVerificationID: verificationId, verificationCode: code)
                return Future<Void, Error> { promise in
                    user.link(with: credential) { _, error in
                        if let error = error {
                            return promise(.failure(error))
                        }
                        promise(.success(()))
                    }
                }
                .eraseToAnyPublisher()
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
    
    private init(_ firebaseAuth: Auth, _ firestore: Firestore) {
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
