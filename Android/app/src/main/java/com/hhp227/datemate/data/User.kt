package com.hhp227.datemate.data

data class User(
    val id: String, // UID
    val username: String, // 이름
    val email: String, // 이메일
    val age: Int, // 나이
    val gender: String, // 성별
    val location: String, // 지역
    val hobby: String, // 취미
    val favorite: String, // 좋아하는것
    val mbti: String // MBTI
)