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
    private val userRepository: UserRepository // UserRepository 주입
    // private val storageRepository: StorageRepository // Storage 관련 Repository 필요
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    fun onNicknameChange(newNickname: String) {
        if (newNickname.length > 20) {
            _uiState.update { it.copy(nicknameError = "닉네임은 20자 이내여야 합니다.") }
        } else {
            _uiState.update { it.copy(nickname = newNickname, nicknameError = null) }
        }
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

    fun completeProfileSetup() {
        val currentState = _uiState.value

        if (currentState.nickname.isBlank() || currentState.nicknameError != null) {
            _uiState.update { it.copy(nicknameError = "유효한 닉네임을 입력해주세요.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // UserRepository.updateUserProfile을 List<Uri>로 호출하도록 변경
            userRepository.updateUserProfile(currentState.selectedImageUris, currentState.nickname)
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _uiState.update { it.copy(isLoading = false, isSetupComplete = true) }
                        }
                        is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = "업데이트 실패: ${resource.message}") }
                        }
                        is Resource.Loading -> { /* 로딩 중 */ }
                    }
                }
        }
    }

    // 네비게이션 후 상태 초기화 (중복 이동 방지)
    fun consumeSetupCompleteEvent() {
        _uiState.update { it.copy(isSetupComplete = false) }
    }
}