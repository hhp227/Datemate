package com.hhp227.datemate.ui.main.discover

import com.hhp227.datemate.data.model.Profile

data class DiscoverUiState(
    val isLoading: Boolean = false,
    val todayRecommendations: List<Profile> = emptyList(),
    val leftProfile: Profile? = null,
    val rightProfile: Profile? = null,
    val selectedProfile: Profile? = null,
    val themedPopular: List<Profile> = emptyList(),
    val themedNewMembers: List<Profile> = emptyList(),
    val themedGlobalFriends: List<Profile> = emptyList(),
    val themedRecentActive: List<Profile> = emptyList(),
    val message: String? = null
)