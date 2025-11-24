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
    private let repository: UserRepository
    
    @Published var uiState = SignUpUiState()
    
    private var subscription = Set<AnyCancellable>()
    
    private func isValidEmail(_ email: String) -> Bool {
        let regex = #"^(.+)@(.+)$"#
        return email.range(of: regex, options: .regularExpression) != nil
    }
    
    func onNameChange(_ newValue: String) {
        uiState.name = newValue
        uiState.nameError = nil
    }

    func onEmailChange(_ newValue: String) {
        uiState.email = newValue
        uiState.emailError = nil
    }

    func onPasswordChange(_ newValue: String) {
        uiState.password = newValue
        uiState.passwordError = nil
    }

    func onConfirmPasswordChange(_ newValue: String) {
        uiState.confirmPassword = newValue
        uiState.confirmPasswordError = nil
    }
    
    func signUp(email: String, password: String) {
        let current = uiState
        
        if current.name.isEmpty {
            uiState.nameError = "이름을 입력해주세요."
            return
        }
        if current.email.isEmpty || !isValidEmail(current.email) {
            uiState.emailError = "유효한 이메일을 입력해주세요."
            return
        }
        if current.password.count < 6 {
            uiState.passwordError = "비밀번호는 6자 이상이어야 합니다."
            return
        }
        if current.password != current.confirmPassword {
            uiState.confirmPasswordError = "비밀번호가 일치하지 않습니다."
            return
        }
        uiState.isLoading = true
        /*userRepository.getSignUpResultStream(email: current.email, password: current.password)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] resource in
                guard let self = self else { return }
                switch resource {
                case .loading:
                    self.uiState.isLoading = true
                    self.uiState.errorMessage = nil
                case .success:
                    self.uiState.isLoading = false
                    self.uiState.isSignUpSuccess = true
                case .error(let message):
                    self.uiState.isLoading = false
                    self.uiState.errorMessage = message
                }
            }
            .store(in: &cancellables)*/
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            self.uiState.isLoading = false
            self.uiState.isSignUpSuccess = true
        }
    }
    
    func consumeSuccessEvent() {
        uiState.isSignUpSuccess = false
    }
    
    init(_ repository: UserRepository) {
        self.repository = repository
    }
    
    deinit {
        subscription.removeAll()
    }
}
