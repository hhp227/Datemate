package com.hhp227.datemate.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class StorageRemoteDataSource private constructor(private val storage: FirebaseStorage) {
    fun uploadFile(fileUri: Uri, path: String): Flow<String> = flow {
        try {
            val storageRef = storage.reference.child(path)
            storageRef.putFile(fileUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            emit(downloadUrl)
        } catch (e: Exception) {
            throw e
        }
    }

    companion object {
        @Volatile
        private var instance: StorageRemoteDataSource? = null

        fun getInstance(storage: FirebaseStorage): StorageRemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: StorageRemoteDataSource(storage).also { instance = it }
            }
    }
}