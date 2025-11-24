//
//  ProfileSetupViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import Foundation
import Combine

class ProfileSetupViewModel: ObservableObject {
    private let userRepository: UserRepository
    
    @Published var uiState = ProfileSetupUiState()
    
    private var cancellables = Set<AnyCancellable>()
    
    func onNicknameChange(_ newNickname: String) {
        if newNickname.count > 20 {
            uiState.nicknameError = "닉네임은 20자 이내여야 합니다."
        } else {
            uiState.nickname = newNickname
            uiState.nicknameError = nil
        }
    }
        
    func onImagesSelected(_ newUrls: [URL]) {
        let combined = (uiState.selectedImageUrls + newUrls).removingDuplicates()
        uiState.selectedImageUrls = combined
    }
        
    func onGenderSelected(_ gender: Gender) {
        uiState.selectedGender = gender
    }
        
    func removeImage(at index: Int) {
        guard index >= 0 && index < uiState.selectedImageUrls.count else { return }
        uiState.selectedImageUrls.remove(at: index)
    }
        
    func completeProfileSetup() {
        if uiState.nickname.isEmpty || uiState.nicknameError != nil {
            uiState.nicknameError = "유효한 닉네임을 입력해주세요."
            return
        }
        uiState.isLoading = true
        uiState.errorMessage = nil
            
        /*Task {
            // async/await 기반으로 Resource 처리
            for await resource in userRepository.updateUserProfile(urls: uiState.selectedImageUrls, nickname: uiState.nickname) {
                await MainActor.run {
                    switch resource {
                    case .success(_):
                        uiState.isLoading = false
                        uiState.isSetupComplete = true
                    case .error(let message):
                        uiState.isLoading = false
                        uiState.errorMessage = "업데이트 실패: \(message)"
                    case .loading:
                        break
                    }
                }
            }
        }*/
    }
        
    func consumeSetupCompleteEvent() {
        uiState.isSetupComplete = false
    }
    
    
    init(_ userRepository: UserRepository) {
        self.userRepository = userRepository
    }
}

extension Array where Element: Hashable {
    func removingDuplicates() -> [Element] {
        var set = Set<Element>()
        return filter { set.insert($0).inserted }
    }
}
