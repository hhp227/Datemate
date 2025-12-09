package com.hhp227.datemate.data.repository

import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.datasource.MatchRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class MatchRepository private constructor(
    private val matchRemoteDataSource: MatchRemoteDataSource
) {
    fun createMatchResultStream(userId: String, selectedId: String): Flow<Resource<Boolean>> {
        return flow {
            try {
                matchRemoteDataSource.createMatch(userId, selectedId)
                emit(Resource.Success(true))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "매칭 실패"))
            }
        }
            .onStart { emit(Resource.Loading()) }
    }

    companion object {
        @Volatile private var instance: MatchRepository? = null

        fun getInstance(matchRemoteDataSource: MatchRemoteDataSource) =
            instance ?: synchronized(this) {
                instance ?: MatchRepository(matchRemoteDataSource).also { instance = it }
            }
    }
}