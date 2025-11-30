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
    
    private let userLocalDataSource: UserLocalDataSource
    
    private let storageRepository: StorageRepository
    
    private var cancellables = Set<AnyCancellable>()
    
    @Published var signInState: SignInState = .loading
    
    var signInStatePublisher: AnyPublisher<SignInState, Never> {
        $signInState.eraseToAnyPublisher()
    }
    
    var remoteUserStatePublisher: AnyPublisher<User?, Never> {
        userRemoteDataSource.userStatePublisher
    }

    var localUserStatePublisher: AnyPublisher<UserCache?, Never> {
        userLocalDataSource.userPublisher
    }
    
    func getSignInResultStream(email: String, password: String) -> AnyPublisher<Resource<FirebaseAuth.User>, Never> {
        userRemoteDataSource.signIn(email: email, password: password).asResource()
    }
        
    func getSignUpResultStream(email: String, password: String) -> AnyPublisher<Resource<FirebaseAuth.User>, Never> {
        userRemoteDataSource.signUp(email: email, password: password).asResource()
    }

    func getSignOutResultStream() -> AnyPublisher<Resource<Bool>, Never> {
        userRemoteDataSource.signOut().asResource()
    }
        
    func getPasswordResetResultStream(email: String) -> AnyPublisher<Resource<Bool>, Never> {
        userRemoteDataSource.sendPasswordResetEmail(email: email).asResource()
    }
    
    func fetchUserProfile(userId: String) -> AnyPublisher<Resource<Profile?>, Never> {
        userRemoteDataSource.fetchUserProfile(userId: userId).asResource()
    }
    
    func createUserProfile(userId: String, email: String?) -> AnyPublisher<Resource<Bool>, Never> {
        userRemoteDataSource.createUserProfile(userId: userId, email: email).asResource()
    }

    func storeUserProfile(_ cache: UserCache?) {
        userLocalDataSource.storeUser(cache)
    }
    
    func updateUserProfile(
        images: [URL],
        name: String,
        gender: String,
        birthdayMillis: Int64,
        bio: String,
        job: String,
        concurrency: Int = 4,
        retryCount: Int = 1
    ) -> AnyPublisher<Resource<String>, Never> {
        userRemoteDataSource.userStatePublisher
            .compactMap { $0 }
            .flatMap(maxPublishers: .max(1)) { user in
                self.storageRepository.uploadAllImages(
                    imageUrls: images,
                    userId: user.uid,
                    concurrency: concurrency,
                    retryCount: retryCount
                )
                .flatMap { uploadedUrls in
                    self.userRemoteDataSource.updateUserProfile(
                        userId: user.uid,
                        name: name,
                        gender: gender,
                        birthdayMillis: birthdayMillis,
                        bio: bio,
                        job: job,
                        profileImageUrls: uploadedUrls
                    )
                }
            }
            .asResource()
    }
    
    func sendOtp(phoneNumber: String) -> AnyPublisher<Resource<String>, Never> {
        userRemoteDataSource.sendOtp(phoneNumber: phoneNumber).asResource()
    }

    func verifyOtp(verificationId: String, code: String) -> AnyPublisher<Resource<Void>, Never> {
        userRemoteDataSource.verifyOtp(verificationId: verificationId, code: code).asResource()
    }
    
    init(
        _ userRemoteDataSource: UserRemoteDataSource,
        _ userLocalDataSource: UserLocalDataSource,
        _ storageRepository: StorageRepository
    ) {
        self.userRemoteDataSource = userRemoteDataSource
        self.userLocalDataSource = userLocalDataSource
        self.storageRepository = storageRepository
        
        userLocalDataSource.userPublisher
            .map { $0 != nil ? SignInState.signIn : SignInState.signOut }
            .prepend(.loading)
            .receive(on: DispatchQueue.main)
            .assign(to: \.signInState, on: self)
            .store(in: &cancellables)
    }
    
    private static var instance: UserRepository? = nil
    
    static func getInstance(
        userRemoteDataSource: UserRemoteDataSource,
        userLocalDataSource: UserLocalDataSource,
        storageRepository: StorageRepository
    ) -> UserRepository {
        if let instance = self.instance {
            return instance
        } else {
            let userRepository = UserRepository(
                userRemoteDataSource,
                userLocalDataSource,
                storageRepository
            )
            self.instance = userRepository
            return userRepository
        }
    }
}

enum SignInState {
    case signIn
    case signOut
    case loading
}
