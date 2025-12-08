package com.hhp227.datemate.data.model

import java.security.Timestamp

data class Match(
    val matchId: String = "",
    val user1: String = "",
    val user2: String = "",
    val createdAt: Timestamp? = null,   // 매칭된 시간
    val isActive: Boolean = true,       // 향후 unmatch 대비
    val lastActionAt: Timestamp? = null // 매칭 후 활동/갱신 시간
)