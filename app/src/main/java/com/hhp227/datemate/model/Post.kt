package com.hhp227.datemate.model

import com.google.firebase.database.IgnoreExtraProperties

data class Post(
    var uid: String = "",
    var author: String = "",
    var title: String = "",
    var body: String = "",
    var starCount: Int = 0,
    var stars: Map<String, Boolean> = mapOf()
)