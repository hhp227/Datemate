package com.hhp227.datemate.data.model

import com.google.firebase.Timestamp

data class User(
    val userId: String = "",
    val fullName: String = "",       // FullName으로 변경됨
    val gender: String = "",
    val birthdayMillis: Long = 0L,   // Long 타입 (밀리초)으로 저장
    val bio: String = "",
    val job: String = "",
    val profileImageUrls: List<String> = emptyList(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)