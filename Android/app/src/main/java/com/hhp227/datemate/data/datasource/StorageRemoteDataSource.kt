package com.hhp227.datemate.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageRemoteDataSource private constructor(
    private val storage: FirebaseStorage
) {
    suspend fun uploadFile(uri: Uri, path: String): String {
        val storageRef = storage.reference.child(path)

        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
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