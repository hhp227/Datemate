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
    
    private let storageRepository: StorageRepository
    
    private var cancellables = Set<AnyCancellable>()
    
    @Published var signInState: SignInState = .loading
    
    var signInStatePublisher: AnyPublisher<SignInState, Never> {
        $signInState.eraseToAnyPublisher()
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
        
    func updateUserProfile(
        imageUrls: [URL],
        fullName: String,
        gender: String,
        birthdayMillis: Int64,
        bio: String,
        job: String,
        concurrency: Int = 4,
        retryCount: Int = 0
    ) -> AnyPublisher<Resource<Bool>, Never> {
        return userRemoteDataSource.userStatePublisher
            .compactMap { $0 }
            .flatMap { [weak self] user -> AnyPublisher<Bool, Error> in
                guard let self else {
                    return Fail(error: NSError(domain: "RepositoryError", code: -1)).eraseToAnyPublisher()
                }
                return self.storageRepository
                    .uploadAllImages(imageUrls: imageUrls, userId: user.uid, concurrency: concurrency, retryCount: retryCount)
                    .flatMap { uploadedUrls in
                        self.userRemoteDataSource.updateUserProfile(
                            userId: user.uid,
                            fullName: fullName,
                            gender: gender,
                            birthdayMillis: birthdayMillis,
                            bio: bio,
                            job: job,
                            profileImageUrls: uploadedUrls
                        )
                    }
                    .eraseToAnyPublisher()
            }
            .asResource()
    }
    
    init(
        _ userRemoteDataSource: UserRemoteDataSource,
        _ storageRepository: StorageRepository
    ) {
        self.userRemoteDataSource = userRemoteDataSource
        self.storageRepository = storageRepository
        
        userRemoteDataSource.userStatePublisher
            .map { $0 != nil ? SignInState.signIn : SignInState.signOut }
            .prepend(.loading)
            .receive(on: DispatchQueue.main)
            .assign(to: \.signInState, on: self)
            .store(in: &cancellables)
    }
    
    private static var instance: UserRepository? = nil
    
    static func getInstance(
        userRemoteDataSource: UserRemoteDataSource,
        storageRepository: StorageRepository
    ) -> UserRepository {
        if let instance = self.instance {
            return instance
        } else {
            let userRepository = UserRepository(userRemoteDataSource, storageRepository)
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
