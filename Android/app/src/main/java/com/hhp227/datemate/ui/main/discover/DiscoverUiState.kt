package com.hhp227.datemate.ui.main.discover

import com.hhp227.datemate.data.model.Profile

data class DiscoverUiState(
    val isLoading: Boolean = false,
    val todayRecommendations: List<Profile> = emptyList(),
    val leftProfile: Profile? = null,
    val rightProfile: Profile? = null,
    val selectedProfile: Profile? = null,
    val themedRecommendations: List<Profile> = emptyList(),
    val message: String? = null
)

