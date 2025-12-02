package com.hhp227.datemate.ui.main.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.repository.ProfileRepository
import com.hhp227.datemate.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DiscoverViewModel internal constructor(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private fun loadTodayRecommendations() {
        viewModelScope.launch {
            userRepository.remoteUserStateFlow
                .filterNotNull()
                .flatMapLatest { user ->
                    profileRepository.getTodayRecommendations(user.uid)
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

    init {
        loadTodayRecommendations()
    }
}