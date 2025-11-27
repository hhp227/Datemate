//
//  DependencyContainer.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/14.
//

import Foundation
import FirebaseAuth
import FirebaseFirestore
import FirebaseStorage
import Combine

final class DependencyContainer {
    let firebaseAuth: Auth
    
    let firestore: Firestore
    
    let storage: Storage
    
    let storageRemoteDataSource: StorageRemoteDataSource
    
    let storageRepository: StorageRepository
    
    let userRemoteDataSource: UserRemoteDataSource
    
    let userRepository: UserRepository
    
    func provideSignInViewModel() -> SignInViewModel {
        return SignInViewModel(userRepository)
    }
    
    func provideSignUpViewModel() -> SignUpViewModel {
        return SignUpViewModel(userRepository)
    }
    
    func provideProfileSetupViewModel() -> ProfileSetupViewModel {
        return ProfileSetupViewModel(userRepository)
    }
    
    func provideForgotPasswordViewModel() -> ForgotPasswordViewModel {
        return ForgotPasswordViewModel(userRepository)
    }
    
    func provideDetailViewModel(data: String) -> SubFirstViewModel {
        return SubFirstViewModel(userRepository, data: data)
    }
    
    private init() {
        self.firebaseAuth = Auth.auth()
        self.firestore = Firestore.firestore()
        self.storage = Storage.storage()
        self.storageRemoteDataSource = StorageRemoteDataSource.getInstance(storage: storage)
        self.storageRepository = StorageRepository.getInstance(storageRemoteDataSource: storageRemoteDataSource)
        self.userRemoteDataSource = UserRemoteDataSource.getInstance(auth: firebaseAuth)
        self.userRepository = UserRepository.getInstance(
            userRemoteDataSource: userRemoteDataSource,
            storageRepository: storageRepository
        )
    }
    
    static let instance = DependencyContainer.init()
}
