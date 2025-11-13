//
//  RegisterViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2021/11/28.
//

import Foundation
import Combine
import FirebaseAuth

class SignUpViewModel: ObservableObject {
    private let repository: UserRepository2
    
    private var subscription = Set<AnyCancellable>()
    
    private func onReceive(_ completion: Subscribers.Completion<Error>) {
        switch completion {
        case .finished:
            break
        case .failure:
            break
        }
    }
    
    func onReceive(_ result: AuthDataResult) {
        print("onReceive: \(result)")
    }
    
    func signUp(email: String, password: String) {
        guard !email.isEmpty, !password.isEmpty else {
            return
        }
        repository.signUp(email: email, password: password).sink(receiveCompletion: onReceive, receiveValue: onReceive).store(in: &subscription)
    }
    
    init(_ repository: UserRepository2) {
        self.repository = repository
    }
    
    deinit {
        subscription.removeAll()
    }
}
