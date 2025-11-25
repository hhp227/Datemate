package com.hhp227.datemate.data.repository

import android.net.Uri
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.datasource.StorageRemoteDataSource

class StorageRepository private constructor(private val remoteDataSource: StorageRemoteDataSource) {
    suspend fun uploadProfileImage(uri: Uri, userId: String, index: Int): Resource<String> {
        // Firebase Storage 경로: users/{userId}/gallery_0_timestamp.jpg
        val path = "users/$userId/gallery_${index}_${System.currentTimeMillis()}.jpg"
        return remoteDataSource.uploadFile(uri, path)
    }

    companion object {
        @Volatile private var instance: StorageRepository? = null

        fun getInstance(remoteDataSource: StorageRemoteDataSource): StorageRepository =
            instance ?: synchronized(this) {
                instance ?: StorageRepository(remoteDataSource).also { instance = it }
            }
    }
}