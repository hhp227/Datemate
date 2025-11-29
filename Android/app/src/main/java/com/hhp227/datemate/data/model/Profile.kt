package com.hhp227.datemate.data.model

import com.google.firebase.Timestamp

data class Profile(
    var uid: String = "",
    val name: String = "",
    val gender: String = "",
    val bio: String = "",
    val birthday: Timestamp? = null,
    val job: String = "",
    val photos: List<String> = emptyList(),
    val updatedAt: Timestamp? = null
)
