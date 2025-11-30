//
//  PhoneAuthViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/30.
//

import Foundation
import Combine

class PhoneAuthViewModel: ObservableObject {
    let userRepository: UserRepository
    
    private(set) var verificationId: String? = nil
    
    @Published var uiState = PhoneAuthUiState()
    
    private var cancellables = Set<AnyCancellable>()
    
    func sendOtp(_ phoneNumber: String) {
        userRepository.sendOtp(phoneNumber: phoneNumber)
            .receive(on: DispatchQueue.main)
            .sink { resource in
                switch resource.state {
                case .Success:
                    self.verificationId = resource.data
                    self.uiState.isLoading = false
                    self.uiState.isCodeSent = true
                case .Error:
                    self.uiState.isLoading = false
                    self.uiState.errorMessage = resource.message
                case .Loading:
                    self.uiState.isLoading = true
                    self.uiState.errorMessage = nil
                }
            }
            .store(in: &cancellables)
    }
    
    func verifyOtp(_ code: String) {
        guard let id = verificationId else { return }
        
        userRepository.verifyOtp(verificationId: id, code: code)
            .receive(on: DispatchQueue.main)
            .sink { resource in
                switch resource.state {
                case .Loading:
                    self.uiState.isLoading = true
                    self.uiState.errorMessage = nil
                case .Success:
                    self.uiState.isLoading = false
                    self.uiState.isVerified = true
                case .Error:
                    self.uiState.isLoading = false
                    self.uiState.errorMessage = resource.message
                }
            }
            .store(in: &cancellables)
    }
    
    init(_ userRepository: UserRepository) {
        self.userRepository = userRepository
    }
}
