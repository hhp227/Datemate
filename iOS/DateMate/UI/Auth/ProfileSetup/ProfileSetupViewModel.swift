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
    
    func onNameChange(_ newName: String) {
        if newName.count > 50 {
            uiState.nameError = "이름은 50자 이내여야 합니다."
        } else {
            uiState.name = newName
            uiState.nameError = nil
        }
    }
    
    func onBirthdaySelected(_ newDateMillis: Int64) {
        uiState.birthdayMillis = newDateMillis
        uiState.birthdayError = nil
    }
        
    func onImagesSelected(_ newUrls: [URL]) {
        let combined = (uiState.selectedImageUrls + newUrls).removingDuplicates()
        uiState.selectedImageUrls = combined
    }
        
    func onGenderSelected(_ gender: Gender) {
        uiState.selectedGender = gender
    }
    
    func onBioChange(_ newBio: String) {
        let trimmed = newBio.trimmingCharacters(in: .whitespacesAndNewlines)
        
        if trimmed.isEmpty {
            uiState.bioError = "자기소개를 입력해주세요."
        } else if trimmed.count > 500 {
            uiState.bioError = "자기소개는 500자 이내로 입력해주세요."
        } else {
            uiState.bioError = nil
        }
        uiState.bio = newBio
    }
    
    func onJobChange(_ newJob: String) {
        let trimmed = newJob.trimmingCharacters(in: .whitespacesAndNewlines)
        
        if trimmed.isEmpty {
            uiState.jobError = "직업을 입력해주세요."
        } else if trimmed.count > 50 {
            uiState.jobError = "직업은 50자 이내로 입력해주세요."
        } else {
            uiState.jobError = nil
        }
        uiState.job = newJob
    }
    
    func removeImage(_ url: URL) {
        uiState.selectedImageUrls.removeAll { $0 == url }
    }
        
    func completeProfileSetup(_ imageUrls: [URL], _ name: String, _ gender: String, _ birthday: Int64, _ bio: String, _ job: String) {
        userRepository.updateUserProfile(images: imageUrls, name: name, gender: gender, birthdayMillis: birthday, bio: bio, job: job)
            .receive(on: DispatchQueue.main)
            .sink { resource in
                switch resource.state {
                case .Success:
                    let data = resource.data ?? ""
                    let userToStore = UserCache(id: data)
                    
                    self.userRepository.storeUserProfile(userToStore)
                    self.uiState.isLoading = false
                    self.uiState.isSetupComplete = true
                case .Error:
                    self.uiState.isLoading = false
                    self.uiState.errorMessage = "업데이트 실패: \(String(describing: resource.message))"
                case .Loading:
                    self.uiState.isLoading = true
                }
            }
            .store(in: &cancellables)
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
