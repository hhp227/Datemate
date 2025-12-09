package com.hhp227.datemate.data.repository

import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.common.asResource
import com.hhp227.datemate.data.datasource.PostRemoteDataSource
import com.hhp227.datemate.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostRepository private constructor(
    private val postRemoteDataSource: PostRemoteDataSource
) {
    fun getPosts(): Flow<List<Post>> = flow {
        // TODO
        emit(listOf(Post()))
    }

    fun fetchUserPostsResultStream(userId: String): Flow<Resource<List<Post>>> {
        return postRemoteDataSource.fetchUserPosts(userId).asResource()
    }

    companion object {
        @Volatile private var instance: PostRepository? = null
        fun getInstance(postRemoteDataSource: PostRemoteDataSource) =
            instance ?: synchronized(this) {
                instance ?: PostRepository(postRemoteDataSource).also { instance = it }
            }
    }
}