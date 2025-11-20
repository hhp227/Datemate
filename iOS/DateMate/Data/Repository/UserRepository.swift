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
    
    private var cancellables = Set<AnyCancellable>()
    
    @Published var signInState: SignInState = .loading
    
    var signInStatePublisher: AnyPublisher<SignInState, Never> {
        $signInState.eraseToAnyPublisher()
    }
    
    func getSignInResultStream(email: String, password: String) -> AnyPublisher<Resource<User>, Never> {
        userRemoteDataSource.signIn(email: email, password: password)
    }

    func getSignOutResultStream() -> AnyPublisher<Resource<Bool>, Never> {
        userRemoteDataSource.signOut()
    }
    
    init(_ userRemoteDataSource: UserRemoteDataSource) {
        self.userRemoteDataSource = userRemoteDataSource
        
        userRemoteDataSource.userStatePublisher
            .map { $0 != nil ? SignInState.signIn : SignInState.signOut }
            .prepend(.loading)
            .receive(on: DispatchQueue.main)
            .assign(to: \.signInState, on: self)
            .store(in: &cancellables)
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

enum SignInState {
    case signIn
    case signOut
    case loading
}
