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
    private let userRepository: UserRepository
    
    @Published var uiState = SignUpUiState()
    
    private var subscription = Set<AnyCancellable>()
    
    private func isValidEmail(_ email: String) -> Bool {
        let regex = #"^(.+)@(.+)$"#
        return email.range(of: regex, options: .regularExpression) != nil
    }
    
    private func createUserProfile(_ userId: String, _ email: String?) {
        userRepository.createUserProfile(userId: userId, email: email)
            .receive(on: DispatchQueue.main)
            .sink { resource in
                switch resource.state {
                case .Success:
                    self.uiState.isLoading = false
                    self.uiState.isSignUpSuccess = true
                case .Error:
                    self.uiState.isLoading = false
                    self.uiState.errorMessage = "초기 데이터 저장 실패: \(String(describing: resource.message))}"
                case .Loading:
                    self.uiState.isLoading = true
                    self.uiState.errorMessage = nil
                }
            }
            .store(in: &subscription)
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
    
    func signUp(email: String, password: String, confirmPassword: String) {
        if email.isEmpty || !isValidEmail(email) {
            uiState.emailError = "유효한 이메일을 입력해주세요."
            return
        }
        if password.count < 6 {
            uiState.passwordError = "비밀번호는 6자 이상이어야 합니다."
            return
        }
        if password != confirmPassword {
            uiState.confirmPasswordError = "비밀번호가 일치하지 않습니다."
            return
        }
        userRepository.getSignUpResultStream(email: email, password: password)
            .receive(on: DispatchQueue.main)
            .sink { resource in
                switch resource.state {
                case .Loading:
                    self.uiState.isLoading = true
                    self.uiState.errorMessage = nil
                case .Success:
                    if let firebaseUser = resource.data {
                        self.createUserProfile(firebaseUser.uid, firebaseUser.email)
                    }
                case .Error:
                    self.uiState.isLoading = false
                    self.uiState.errorMessage = resource.message
                }
            }
            .store(in: &subscription)
    }
    
    func consumeSuccessEvent() {
        uiState.isSignUpSuccess = false
    }
    
    init(_ userRepository: UserRepository) {
        self.userRepository = userRepository
    }
    
    deinit {
        subscription.removeAll()
    }
}
