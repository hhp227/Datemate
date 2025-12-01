package com.hhp227.datemate.ui.myprofile

import com.hhp227.datemate.data.model.Post
import com.hhp227.datemate.data.model.Profile

data class MyProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val stats: UserStats = UserStats(0, "", ""),
    val posts: List<Post> = emptyList(),
    val message: String? = null
)

data class UserStats(val postCount: Int, val followers: String, val following: String)