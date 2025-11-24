package com.hhp227.datemate.ui.auth.profilesetup

import android.net.Uri
import com.hhp227.datemate.data.model.Gender

data class ProfileSetupUiState(
    val nickname: String = "",
    val selectedImageUris: List<Uri> = emptyList(),
    val selectedGender: Gender? = null,
    val isLoading: Boolean = false,
    val isSetupComplete: Boolean = false,
    val errorMessage: String? = null,
    val nicknameError: String? = null
) {
    val isSubmitEnabled: Boolean
        get() {
            val isNicknameValid = nickname.isNotBlank() && nicknameError == null
            val isPhotoListValid = selectedImageUris.isNotEmpty()
            val isGenderSelected = selectedGender != null

            return isNicknameValid && isPhotoListValid && isGenderSelected
        }
}