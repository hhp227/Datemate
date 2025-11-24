//
//  ProfileSetupUiState.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import Foundation

struct ProfileSetupUiState {
    var nickname: String = ""
    var selectedImageUrls: [URL] = []
    var selectedGender: Gender? = nil
    var isLoading: Bool = false
    var isSetupComplete: Bool = false
    var errorMessage: String? = nil
    var nicknameError: String? = nil
    var isSubmitEnabled: Bool {
        let isNicknameValid = !nickname.isEmpty && nicknameError == nil
        let isPhotoListValid = !selectedImageUrls.isEmpty
        let isGenderSelected = selectedGender != nil
        return isNicknameValid && isPhotoListValid && isGenderSelected
    }
}
