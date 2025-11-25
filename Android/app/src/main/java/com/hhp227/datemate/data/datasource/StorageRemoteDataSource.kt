package com.hhp227.datemate.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.hhp227.datemate.common.Resource
import kotlinx.coroutines.tasks.await

class StorageRemoteDataSource private constructor(private val storage: FirebaseStorage) {
    suspend fun uploadFile(fileUri: Uri, path: String): Resource<String> = try {
        val storageRef = storage.reference.child(path)
        storageRef.putFile(fileUri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()
        Resource.Success(downloadUrl)
    } catch (e: Exception) {
        Resource.Error("파일 업로드 실패: ${e.message}")
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