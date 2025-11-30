//
//  ProfileSetupUiState.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/25.
//

import Foundation

struct ProfileSetupUiState {
    var name: String = ""
    var selectedImageUrls: [URL] = []
    var selectedGender: Gender? = nil
    var bio: String = ""
    var birthdayMillis: Int64? = nil
    var job: String = ""
    var isLoading: Bool = false
    var isSetupComplete: Bool = false
    var errorMessage: String? = nil
    var nameError: String? = nil
    var birthdayError: String? = nil
    var bioError: String? = nil
    var jobError: String? = nil
    var isSubmitEnabled: Bool {
        let isNameValid = !name.isEmpty && nameError == nil
        let isPhotoListValid = !selectedImageUrls.isEmpty
        let isGenderSelected = selectedGender != nil
        let isBirthdayValid = birthdayMillis != nil && birthdayError == nil
        let isBioValid = !bio.isEmpty && bioError == nil
        let isJobValid = !job.isEmpty && jobError == nil
        return isNameValid && isPhotoListValid && isGenderSelected &&
               isBirthdayValid && isBioValid && isJobValid
    }
}
