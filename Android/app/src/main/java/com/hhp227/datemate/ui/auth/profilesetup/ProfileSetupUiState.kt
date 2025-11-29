package com.hhp227.datemate.ui.auth.profilesetup

import android.net.Uri
import com.hhp227.datemate.data.model.Gender

data class ProfileSetupUiState(
    val name: String = "",
    val selectedImageUris: List<Uri> = emptyList(),
    val selectedGender: Gender? = null,
    val bio: String = "",
    val birthdayMillis: Long? = null,
    val job: String = "",
    val isLoading: Boolean = false,
    val isSetupComplete: Boolean = false,
    val errorMessage: String? = null,
    val nameError: String? = null,
    val birthdayError: String? = null, // 생년월일 에러 추가
    val bioError: String? = null,   // 🆕 자기소개 오류 상태 추가
    val jobError: String? = null    // 🆕 직업 오류 상태 추가
) {
    val isSubmitEnabled: Boolean
        get() {
            val isNameValid = name.isNotBlank() && nameError == null
            val isPhotoListValid = selectedImageUris.isNotEmpty()
            val isGenderSelected = selectedGender != null
            val isBirthdayValid = birthdayError == null
            val isBioValid = bio.isNotBlank() && bioError == null && bio.length <= 200 // Bio 최대 길이 유효성 검사 추가 (200자)
            val isJobValid = job.isNotBlank() && jobError == null
            return isNameValid && isPhotoListValid && isGenderSelected && isBioValid && isBirthdayValid && isJobValid
        }
}