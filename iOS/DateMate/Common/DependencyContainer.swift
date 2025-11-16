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
    
    /*func provideUserViewModel() -> UserViewModel {
        return UserViewModel(userRepository: userRepository)
    }*/
    
    // 사용: @StateObject private var viewModel = DependencyContainer.shared.provideUserViewModel()
    
    // Temp
    @Published private var _isLoggedIn: Bool = false
    var isLoggedInPublisher: AnyPublisher<Bool, Never> {
        return $_isLoggedIn.eraseToAnyPublisher()
    }
    var isLoggedIn: Bool { _isLoggedIn }

    func set(_ value: Bool) {
        _isLoggedIn = value
    }
    
    private init() {
        self.firebaseAuth = Auth.auth()
        self.userRemoteDataSource = UserRemoteDataSource.getInstance(auth: firebaseAuth)
        self.userRepository = UserRepository.getInstance(userRemoteDataSource: userRemoteDataSource)
    }
    
    static let instance = DependencyContainer.init()
}
