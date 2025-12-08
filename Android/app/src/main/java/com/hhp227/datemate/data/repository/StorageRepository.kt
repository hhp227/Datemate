package com.hhp227.datemate.data.repository

import android.net.Uri
import android.util.Log
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.datasource.StorageRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Semaphore

class StorageRepository private constructor(
    private val storageRemoteDataSource: StorageRemoteDataSource
) {
    private suspend fun uploadProfileImage(uri: Uri, userId: String, index: Int): String {
        val path = "users/$userId/gallery_${index}_${System.currentTimeMillis()}.jpg"
        return storageRemoteDataSource.uploadFile(uri, path)
    }

    fun uploadAllImages(imageUris: List<Uri>, userId: String, concurrency: Int = 3): Flow<Resource<List<String>>> = flow {
        try {
            val semaphore = Semaphore(concurrency)
            val urls = coroutineScope {
                imageUris.mapIndexed { index, uri ->
                    async(Dispatchers.IO) {
                        semaphore.acquire()
                        try {
                            uploadProfileImage(uri, userId, index)
                        } finally {
                            semaphore.release()
                        }
                    }
                }.awaitAll()
            }

            emit(Resource.Success(urls))
        } catch (e: Exception) {
            emit(Resource.Error<List<String>>(e.message ?: "Upload failed"))
        }
    }
        .onStart { emit(Resource.Loading()) }

    companion object {
        @Volatile private var instance: StorageRepository? = null

        fun getInstance(remote: StorageRemoteDataSource): StorageRepository =
            instance ?: synchronized(this) {
                instance ?: StorageRepository(remote).also { instance = it }
            }
    }
}