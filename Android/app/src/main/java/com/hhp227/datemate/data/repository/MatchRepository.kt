package com.hhp227.datemate.data.repository

import com.hhp227.datemate.data.datasource.MatchRemoteDataSource

class MatchRepository private constructor(
    private val matchRemoteDataSource: MatchRemoteDataSource
) {
    suspend fun createMatch(userId: String, selectedId: String) {
        matchRemoteDataSource.createMatch(userId, selectedId)
    }

    companion object {
        @Volatile private var instance: MatchRepository? = null

        fun getInstance(matchRemoteDataSource: MatchRemoteDataSource) =
            instance ?: synchronized(this) {
                instance ?: MatchRepository(matchRemoteDataSource).also { instance = it }
            }
    }
}