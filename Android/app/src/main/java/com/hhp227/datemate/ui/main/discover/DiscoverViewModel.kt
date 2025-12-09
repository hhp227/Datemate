package com.hhp227.datemate.ui.main.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.model.Profile
import com.hhp227.datemate.data.repository.MatchRepository
import com.hhp227.datemate.data.repository.RecommendationRepository
import com.hhp227.datemate.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DiscoverViewModel internal constructor(
    private val userRepository: UserRepository,
    private val recommendationRepository: RecommendationRepository,
    private val matchRepository: MatchRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(DiscoverUiState(isLoading = true))
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private fun loadRecommendations() {
        viewModelScope.launch {
            userRepository.remoteUserStateFlow
                .filterNotNull()
                .flatMapLatest { user ->
                    recommendationRepository.getAllRecommendationsResultStream(user.uid)
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Error<*> -> {
                            _uiState.update { it.copy(isLoading = false, message = resource.message) }
                        }
                        is Resource.Loading<*> -> {
                            _uiState.update { it.copy(isLoading = true, message = null) }
                        }
                        is Resource.Success<*> -> {
                            val result = resource.data ?: return@collect

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    todayRecommendations = result.todayRecommendations,
                                    leftProfile = result.todayChoices.left,
                                    rightProfile = result.todayChoices.right,
                                    selectedProfile = result.todayChoices.selected,
                                    themedPopular = result.themedPopular,
                                    themedNewMembers = result.themedNewMembers,
                                    themedGlobalFriends = result.themedGlobalFriends,
                                    themedRecentActive = result.themedRecentActive,
                                    message = it.message
                                )
                            }
                        }
                    }
                }
        }
    }

    fun selectChoice(profile: Profile) {
        viewModelScope.launch {
            val currentUser = userRepository.remoteUserStateFlow.first() ?: return@launch

            // 1) 애니메이션을 위해 로컬 상태만 먼저 변경
            _uiState.update { it.copy(selectedProfile = profile) }

            // 2) 애니메이션 시간 기다림 (약 450ms)
            delay(450)

            // 3) 서버 저장
            recommendationRepository.selectTodayChoiceResultStream(currentUser.uid, profile.uid)
                .collect { res ->
                    when (res) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false, message = res.message) }
                        }
                        is Resource.Success -> {
                            _uiState.update { it.copy(isLoading = false) }

                            // 4) 강제 매칭 생성 (오늘의 초이스는 무조건 매칭)
                            matchRepository.createMatchResultStream(currentUser.uid, profile.uid).first()
                        }
                    }
                }
        }
    }

    init {
        loadRecommendations()
    }
}