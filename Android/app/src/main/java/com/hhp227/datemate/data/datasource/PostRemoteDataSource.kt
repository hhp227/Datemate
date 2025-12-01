package com.hhp227.datemate.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.hhp227.datemate.data.model.Post
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostRemoteDataSource private constructor(
    private val firestore: FirebaseFirestore
) {
    // 유저의 ID를 기반으로 게시물 목록을 가져옴 (현재는 Mock Data)
    fun fetchUserPosts(userId: String): Flow<List<Post>> = flow {
        delay(500) // 네트워크 지연 시뮬레이션

        val mockPosts = listOf(
            Post("1", userId, "Traditional spare ribs baked", "Chef John", listOf("https://picsum.photos/400/200?random=1")),
            Post("2", userId, "Spice roasted chicken with flavored rice", "Mark Kelvin", listOf("https://picsum.photos/400/200?random=2")),
            Post("3", userId, "Spicy fried rice with bacon", "Chef Anna", listOf("https://picsum.photos/400/200?random=3")),
            Post("4", userId, "Classic Beef Wellington", "Gordon", listOf("https://picsum.photos/400/200?random=4"))
        )
        emit(mockPosts)
    }

    companion object {
        @Volatile private var instance: PostRemoteDataSource? = null

        fun getInstance(firestore: FirebaseFirestore) =
            instance ?: synchronized(this) {
                instance ?: PostRemoteDataSource(firestore).also { instance = it }
            }
    }
}