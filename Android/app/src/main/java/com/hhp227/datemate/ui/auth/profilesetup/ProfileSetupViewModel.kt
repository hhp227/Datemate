package com.hhp227.datemate.ui.auth.profilesetup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.model.Gender
import com.hhp227.datemate.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileSetupViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    fun onFullNameChange(newFullName: String) {
        if (newFullName.length > 50) {
            _uiState.update { it.copy(fullNameError = "이름은 50자 이내여야 합니다.") }
        } else {
            _uiState.update { it.copy(fullName = newFullName, fullNameError = null) }
        }
    }

    fun onBirthdaySelected(newDateMillis: Long) {
        _uiState.update { it.copy(birthdayMillis = newDateMillis, birthdayError = null) }
    }

    fun onImagesSelected(newUris: List<Uri>) {
        _uiState.update {
            it.copy(selectedImageUris = (it.selectedImageUris + newUris).distinct())
        }
    }

    fun onGenderSelected(gender: Gender) {
        _uiState.update {
            it.copy(
                selectedGender = gender
            )
        }
    }

    fun removeImage(uri: Uri) {
        _uiState.update {
            it.copy(selectedImageUris = it.selectedImageUris.filter { item -> item != uri })
        }
    }

    fun onBioChange(newBio: String) {
        val bioTrimmed = newBio.trim()
        val bioError = when {
            bioTrimmed.isBlank() -> "자기소개를 입력해주세요."
            bioTrimmed.length > 500 -> "자기소개는 500자 이내로 입력해주세요."
            else -> null
        }
        _uiState.update { it.copy(bio = newBio, bioError = bioError) }
    }

    fun onJobChange(newJob: String) {
        val jobTrimmed = newJob.trim()
        val jobError = when {
            jobTrimmed.isBlank() -> "직업을 입력해주세요."
            jobTrimmed.length > 50 -> "직업은 50자 이내로 입력해주세요."
            else -> null
        }
        _uiState.update { it.copy(job = newJob, jobError = jobError) }
    }

    fun completeProfileSetup() {
        val currentState = _uiState.value

        if (currentState.fullName.isBlank() || currentState.fullNameError != null) {
            _uiState.update { it.copy(fullNameError = "유효한 이름을 입력해주세요.") }
            return
        }
        if (currentState.birthdayMillis == null || currentState.birthdayError != null) {
            _uiState.update { it.copy(birthdayError = "유효한 생년월일을 선택해주세요.") }
            return
        }
        viewModelScope.launch {
            userRepository.updateUserProfile(
                currentState.selectedImageUris,
                currentState.fullName,
                currentState.selectedGender?.name.toString(),
                currentState.birthdayMillis,
                currentState.bio,
                currentState.job
            )
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _uiState.update { it.copy(isLoading = false, isSetupComplete = true) }
                        }
                        is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = "업데이트 실패: ${resource.message}") }
                        }
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
        }
    }

    // 네비게이션 후 상태 초기화 (중복 이동 방지)
    fun consumeSetupCompleteEvent() {
        _uiState.update { it.copy(isSetupComplete = false) }
    }
}