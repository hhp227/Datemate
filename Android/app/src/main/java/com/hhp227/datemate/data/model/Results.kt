package com.hhp227.datemate.data.model

data class RecommendationResult(
    val todayRecommendations: List<Profile>,
    val todayChoices: TodayChoice,
    val themedPopular: List<Profile>,
    val themedNewMembers: List<Profile>,
    val themedGlobalFriends: List<Profile>,
    val themedRecentActive: List<Profile>
)

data class TodayChoice(
    val left: Profile?,
    val right: Profile?,
    val selected: Profile?
)