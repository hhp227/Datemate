package com.hhp227.datemate.data.repository

import android.net.Uri
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.common.asResource
import com.hhp227.datemate.data.datasource.StorageRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.toList

class StorageRepository private constructor(
    private val remoteDataSource: StorageRemoteDataSource,
    private val defaultConcurrency: Int = 4
) {
    fun uploadProfileImageFlow(uri: Uri, userId: String, index: Int): Flow<Resource<String>> {
        val path = "users/$userId/gallery_${index}_${System.currentTimeMillis()}.jpg"
        return remoteDataSource.uploadFile(uri, path).asResource()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun uploadAllImages(
        imageUris: List<Uri>,
        userId: String,
        concurrency: Int = defaultConcurrency,
        retryCount: Int = 0
    ): Flow<List<String>> = flow {
        val flows = imageUris.mapIndexed { index, uri ->
            remoteDataSource.uploadFile(uri, "users/$userId/gallery_${index}_${System.currentTimeMillis()}.jpg")
                .retry(retryCount.toLong())
                .map { it }
        }
        val urls = flows
            .asFlow()
            .flattenMerge(concurrency)
            .toList()
        emit(urls)
    }

    companion object {
        @Volatile private var instance: StorageRepository? = null

        fun getInstance(remoteDataSource: StorageRemoteDataSource): StorageRepository =
            instance ?: synchronized(this) {
                instance ?: StorageRepository(remoteDataSource).also { instance = it }
            }
    }
}