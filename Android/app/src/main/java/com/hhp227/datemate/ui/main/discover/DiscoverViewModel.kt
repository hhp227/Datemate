package com.hhp227.datemate.ui.main.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.model.Gender
import com.hhp227.datemate.data.model.Profile
import com.hhp227.datemate.data.repository.MatchRepository
import com.hhp227.datemate.data.repository.ProfileRepository
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
    private var _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private fun loadTodayRecommendations() {
        viewModelScope.launch {
            userRepository.remoteUserStateFlow
                .filterNotNull()
                .flatMapLatest { user ->
                    recommendationRepository.getTodayRecommendations(user.uid)
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
                            val list = resource.data ?: emptyList()

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    todayRecommendations = list,
                                    message = if (list.isNotEmpty()) null else it.message
                                )
                            }
                        }
                    }
                }
        }
    }

    fun loadTodaysChoice() {
        viewModelScope.launch {
            userRepository.remoteUserStateFlow
                .filterNotNull()
                .flatMapLatest { user ->
                    recommendationRepository.getTodaysChoice(user.uid)
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false, message = resource.message) }
                        }
                        is Resource.Success -> {
                            val choice = resource.data

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    leftProfile = choice?.left,
                                    rightProfile = choice?.right,
                                    selectedProfile = choice?.selected
                                )
                            }
                        }
                    }
                }
        }
    }

    fun loadThemedRecommendations() {
        viewModelScope.launch {
            recommendationRepository.getThemedRecommendations()
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false, message = resource.message) }
                        }
                        is Resource.Success -> {
                            val themedRecommendations = resource.data ?: emptyList()

                            _uiState.update {
                                it.copy(
                                    themedRecommendations = themedRecommendations
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
            recommendationRepository.selectTodayChoice(currentUser.uid, profile.uid)

            // 4) 강제 매칭 생성 (오늘의 초이스는 무조건 매칭)
            matchRepository.createMatch(currentUser.uid, profile.uid)
        }
    }

    init {
        loadTodayRecommendations()
        loadTodaysChoice()
        loadThemedRecommendations()
    }
}