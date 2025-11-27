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
        userRemoteDataSource.signIn(email: email, password: password)
    }
    
    func getSignUpResultStream(email: String, password: String) -> AnyPublisher<Resource<FirebaseAuth.User>, Error> {
        userRemoteDataSource.signUp(email: email, password: password)
    }

    func getSignOutResultStream() -> AnyPublisher<Resource<Bool>, Never> {
        userRemoteDataSource.signOut()
    }
    
    func getPasswordResetResultStream(email: String) -> AnyPublisher<Resource<Bool>, Never> {
        userRemoteDataSource.sendPasswordResetEmail(email: email)
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
