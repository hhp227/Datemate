package com.hhp227.datemate.ui.main.discover

import com.hhp227.datemate.data.model.Profile

data class DiscoverUiState(
    val isLoading: Boolean = false,
    val todayRecommendations: List<Profile> = emptyList(),
    val message: String? = null
)