package com.hhp227.datemate.data.model

import com.google.firebase.Timestamp

data class Post(
    var uid: String = "",
    var userId: String = "",
    var title: String = "",
    var content: String = "",
    var imageUrls: List<String> = emptyList(),
    var likeCount: Int = 0,
    var createdAt: Timestamp? = null
)