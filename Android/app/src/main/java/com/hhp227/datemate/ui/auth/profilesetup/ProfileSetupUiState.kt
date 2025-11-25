package com.hhp227.datemate.ui.auth.profilesetup

import android.net.Uri
import com.hhp227.datemate.data.model.Gender

data class ProfileSetupUiState(
    val fullName: String = "", // 닉네임 -> FullName으로 변경
    val selectedImageUris: List<Uri> = emptyList(),
    val selectedGender: Gender? = null,
    val bio: String = "",
    val birthdayMillis: Long? = null,
    val job: String = "",
    val isLoading: Boolean = false,
    val isSetupComplete: Boolean = false,
    val errorMessage: String? = null,
    val fullNameError: String? = null, // nicknameError -> fullNameError 로 변경
    val birthdayError: String? = null, // 생년월일 에러 추가
    val bioError: String? = null,   // 🆕 자기소개 오류 상태 추가
    val jobError: String? = null    // 🆕 직업 오류 상태 추가
) {
    val isSubmitEnabled: Boolean
        get() {
            val isFullNameValid = fullName.isNotBlank() && fullNameError == null // FullName 검사
            val isPhotoListValid = selectedImageUris.isNotEmpty()
            val isGenderSelected = selectedGender != null
            val isBirthdayValid = birthdayMillis != null && birthdayError == null // Long? 으로 검사
            val isBioValid = bio.isNotBlank() && bioError == null    // 🆕 자기소개 유효성 검사
            val isJobValid = job.isNotBlank() && jobError == null
            return isFullNameValid && isPhotoListValid && isGenderSelected && isBioValid && isBirthdayValid && isJobValid
        }
}