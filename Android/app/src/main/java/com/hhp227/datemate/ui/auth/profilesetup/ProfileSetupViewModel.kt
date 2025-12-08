package com.hhp227.datemate.ui.auth.profilesetup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.model.Gender
import com.hhp227.datemate.data.repository.ProfileRepository
import com.hhp227.datemate.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileSetupViewModel(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        if (newName.length > 50) {
            _uiState.update { it.copy(nameError = "이름은 50자 이내여야 합니다.") }
        } else {
            _uiState.update { it.copy(name = newName, nameError = null) }
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

    fun completeProfileSetup(imageUrls: List<Uri>, name: String, gender: String, birthday: Long, bio: String, job: String) {
        viewModelScope.launch {
            val user = userRepository.remoteUserStateFlow.first() ?: throw Exception("로그인 필요")

            profileRepository.updateUserProfile(
                userId = user.uid,
                name = name,
                gender = gender,
                birthday = birthday,
                job = job,
                bio = bio,
                imageUris = imageUrls
            ).collect { result ->
                when (result) {
                    is Resource.Error<*> -> _uiState.update { it.copy(isLoading = false, errorMessage = "업데이트 실패: ${result.message}") }
                    is Resource.Loading<*> -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success<*> -> {
                        _uiState.update { it.copy(isLoading = false, isSetupComplete = result.data == true) }
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