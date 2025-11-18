//
//  DependencyContainer.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/14.
//

import Foundation
import FirebaseAuth
import Combine

final class DependencyContainer {
    let firebaseAuth: Auth
    
    let userRemoteDataSource: UserRemoteDataSource
    
    let userRepository: UserRepository
    
    func provideSignInViewModel() -> SignInViewModel {
        return SignInViewModel(userRepository)
    }
    
    func provideDetailViewModel(data: String) -> SubFirstViewModel {
        return SubFirstViewModel(userRepository, data: data)
    }
    
    // 사용: @StateObject private var viewModel = DependencyContainer.shared.provideUserViewModel()
    
    private init() {
        self.firebaseAuth = Auth.auth()
        self.userRemoteDataSource = UserRemoteDataSource.getInstance(auth: firebaseAuth)
        self.userRepository = UserRepository.getInstance(userRemoteDataSource: userRemoteDataSource)
    }
    
    static let instance = DependencyContainer.init()
}
