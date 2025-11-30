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
    
    private func fetchUserProfile(_ userId: String) {
        userRepository.fetchUserProfile(userId: userId)
            .receive(on: DispatchQueue.main)
            .sink { resource in
                switch resource.state {
                case .Success:
                    let userCache = UserCache(id: userId)
                    
                    if resource.data != nil {
                        self.userRepository.storeUserProfile(userCache)
                        self.uiState.isLoading = false
                        self.uiState.message = nil
                    }
                case .Error:
                    self.uiState.isLoading = false
                    self.uiState.message = "로그인 성공, 프로필 로드 실패. 설정 화면으로 이동합니다."
                case .Loading:
                    self.uiState.isLoading = true
                    self.uiState.message = nil
                }
            }
            .store(in: &cancellables)
    }
    
    func onEmailChanged(_ value: String) {
        uiState.email = value
        uiState.emailError = validateEmail(value)
    }
    
    func onPasswordChanged(_ value: String) {
        uiState.password = value
        uiState.passwordError = validatePassword(value)
    }
    
    func signIn(_ email: String, _ password: String) {
        userRepository.getSignInResultStream(email: email, password: password)
            .receive(on: DispatchQueue.main)
            .sink { result in
                switch result.state {
                case .Loading:
                    self.uiState.isLoading = true
                    self.uiState.message = nil
                case .Success:
                    guard let firebaseUser = result.data else {
                        self.uiState.isLoading = false
                        self.uiState.message = "로그인 성공 후 사용자 정보를 찾을 수 없습니다."
                        return
                    }
                    self.fetchUserProfile(firebaseUser.uid)
                case .Error:
                    self.uiState.isLoading = false
                    self.uiState.message = result.message ?? "알 수 없는 로그인 오류가 발생했습니다."
                }
            }
            .store(in: &cancellables)
    }
    
    init(_ userRepository: UserRepository) {
        self.userRepository = userRepository
        
        // 이미 로그인된 사용자 체크 및 프로필 fetch
        userRepository.remoteUserStatePublisher
            .compactMap { $0 }
            .flatMap(maxPublishers: .max(1)) { user in
                userRepository.fetchUserProfile(userId: user.uid)
            }
            .sink { resource in
                switch resource.state {
                case .Success:
                    self.uiState.isAlreadySignIn = resource.data! == nil
                case .Error:
                    self.uiState.isAlreadySignIn = false
                    self.uiState.message = resource.message
                case .Loading: break
                }
            }
            .store(in: &cancellables)
    }
    
    private static let EMAIL_VALIDATION_REGEX = #"^(.+)@(.+)$"#
}
