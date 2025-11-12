//
//  LoginViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2021/11/28.
//

import Foundation
import FirebaseAuth
import Combine

class SignInViewModel: ObservableObject {
    @Published var signInResult: SignInResult
    
    private let repository: UserRepository
    
    private var subscription = Set<AnyCancellable>()

    private func isEmailValid(_ email: String) -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        return email.contains("@") ? NSPredicate(format: "SELF MATCHES %@", emailRegEx).evaluate(with: email) : !email.isEmpty
    }
    
    private func isPasswordValid(_ password: String) -> Bool {
        return password.count > 3
        }
    
    private func onReceive(_ batch: AuthDataResult?) {
        if batch != nil {
            self.signInResult = SignInResult(true)
        } else {
            self.signInResult = SignInResult(false)
        }
    }
    
    func onReceive(_ completion: Subscribers.Completion<Error>) {
        switch completion {
        case .finished:
            self.signInResult = SignInResult(true)
            break
        case .failure:
            self.signInResult = SignInResult(false)
            break
        }
    }
    
    func signIn(email: String, password: String) {
        guard isEmailValid(email), isPasswordValid(password) else {
            return
        }
        repository.signIn(email: email, password: password).sink(receiveCompletion: onReceive, receiveValue: onReceive).store(in: &subscription)
    }
    
    func signOut() {
        self.signInResult = SignInResult(false)
        
        repository.signOut()
    }
    
    init(_ repository: UserRepository) {
        self.repository = repository
        self.signInResult = SignInResult(repository.getCurrentUser() != nil)
    }
    
    deinit {
        subscription.removeAll()
    }
    
    struct SignInResult {
        let success: Bool
        
        init(_ success: Bool = false) {
            self.success = success
        }
    }
}
