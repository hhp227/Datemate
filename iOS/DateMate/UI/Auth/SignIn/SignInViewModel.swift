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

    private func isEmailValid(_ email: String) -> Bool {
        return email.range(of: SignInViewModel.EMAIL_VALIDATION_REGEX, options: .regularExpression) != nil
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
        uiState.email = value
        uiState.emailError = validateEmail(value)
    }
    
    func onPasswordChanged(_ value: String) {
        uiState.password = value
        uiState.passwordError = validatePassword(value)
    }
    
    func signIn(email: String, password: String) {
        userRepository.getSignInResultStream(email: email, password: password)
            .receive(on: DispatchQueue.main)
            .sink { result in
                switch result.state {
                case .Loading:
                    self.uiState.isLoading = true
                    self.uiState.message = nil
                case .Success:
                    self.uiState.isLoading = false
                    self.uiState.message = nil
                case .Error:
                    self.uiState.isLoading = false
                    self.uiState.message = result.message ?? "알 수 없는 로그인 오류가 발생했습니다."
                }
            }
            .store(in: &cancellables)
    }
    
    init(_ userRepository: UserRepository) {
        self.userRepository = userRepository
    }
    
    private static let EMAIL_VALIDATION_REGEX = #"^(.+)@(.+)$"#
}
