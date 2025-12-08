package com.hhp227.datemate.ui.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.repository.PostRepository
import com.hhp227.datemate.data.repository.ProfileRepository
import com.hhp227.datemate.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MyProfileUiState>(MyProfileUiState())
    val uiState: StateFlow<MyProfileUiState> = _uiState.asStateFlow()

    private fun loadUserProfile() {
        viewModelScope.launch {
            userRepository.remoteUserStateFlow
                .filterNotNull()
                .flatMapLatest { user ->
                    val profileFlow = profileRepository.fetchUserProfile(user.uid)
                    val postsFlow = postRepository.fetchUserPosts(user.uid)

                    combine(profileFlow, postsFlow) { profileResource, postsResource ->
                        if (profileResource is Resource.Loading || postsResource is Resource.Loading) {
                            return@combine MyProfileUiState(isLoading = true)
                        }
                        if (profileResource is Resource.Success && postsResource is Resource.Success) {
                            val profile = profileResource.data
                            val posts = postsResource.data ?: emptyList()
                            val stats = UserStats(
                                postCount = posts.size,
                                followers = "2.5M", // 추후 팔로우 기능 구현 시 교체
                                following = "259"
                            )
                            return@combine MyProfileUiState(profile = profile, stats = stats, posts = posts)
                        } else {
                            return@combine MyProfileUiState(message = "프로필 정보를 불러올 수 없습니다.")
                        }
                    }
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    init {
        loadUserProfile()
    }
}