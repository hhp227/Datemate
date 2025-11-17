//
//  SignInViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2021/11/28.
//

import Foundation
import FirebaseAuth
import Combine

class SignInViewModel: ObservableObject {
    private let userRepository: UserRepository
    
    @Published var uiState = SignInUiState()
    
    private var cancellables = Set<AnyCancellable>()
    
    private func canSubmit(email: String, password: String) -> Bool {
        return !email.isEmpty &&
        validateEmail(email) == nil &&
        !password.isEmpty &&
        validatePassword(password) == nil
    }

    private func isEmailValid(_ email: String) -> Bool {
        let regex = #"^(.+)@(.+)$"#
        return email.range(of: regex, options: .regularExpression) != nil
    }
    
    private func validateEmail(_ value: String) -> String? {
        if value.isEmpty || isEmailValid(value) {
            return nil
        } else {
            return "Invalid email: \(value)"
        }
    }

    private func isPasswordValid(_ password: String) -> Bool {
        return password.count > 3
    }

    private func validatePassword(_ value: String) -> String? {
        if value.isEmpty || isPasswordValid(value) {
            return nil
        } else {
            return "Invalid password"
        }
    }
    
    func onEmailChanged(_ value: String) {
        let password = uiState.password
        uiState.email = value
        uiState.emailError = validateEmail(value)
        uiState.isSignInEnabled = canSubmit(email: value, password: password)
    }
    
    func onPasswordChanged(_ value: String) {
        let email = uiState.email
        uiState.password = value
        uiState.passwordError = validatePassword(value)
        uiState.isSignInEnabled = canSubmit(email: email, password: value)
    }
    
    func signIn(email: String, password: String) {
        userRepository.getSignInResultStream(email: email, password: password)
            .receive(on: DispatchQueue.main)
            .sink { result in
                switch result.state {
                case .Loading:
                    self.uiState.isLoading = true
                case .Success:
                    self.uiState.isLoading = false
                case .Error:
                    self.uiState.isLoading = false
                }
            }
            .store(in: &cancellables)
    }
    
    init(_ userRepository: UserRepository) {
        self.userRepository = userRepository
    }
}
