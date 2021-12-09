package com.hhp227.datemate.model

import com.google.firebase.database.IgnoreExtraProperties

data class Post(
    var uid: String? = null,
    var author: String? = null,
    var title: String? = null,
    var body: String? = null,
    var starCount: Int = 0,
    var stars: Map<String, Boolean> = mapOf()
)