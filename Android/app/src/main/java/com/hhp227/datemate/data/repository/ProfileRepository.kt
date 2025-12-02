package com.hhp227.datemate.data.repository

import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.common.asResource
import com.hhp227.datemate.data.datasource.ProfileRemoteDataSource
import com.hhp227.datemate.data.model.Profile
import kotlinx.coroutines.flow.Flow

class ProfileRepository private constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource
) {
    fun getTodayRecommendations(userId: String): Flow<Resource<List<Profile>>> {
        return profileRemoteDataSource.getTodayRecommendations(userId).asResource()
    }

    companion object {
        @Volatile private var instance: ProfileRepository? = null

        fun getInstance(remote: ProfileRemoteDataSource) =
            instance ?: synchronized(this) {
                instance ?: ProfileRepository(remote).also { instance = it }
            }
    }
}