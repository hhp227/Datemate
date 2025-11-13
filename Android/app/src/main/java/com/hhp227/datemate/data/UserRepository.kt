package com.hhp227.datemate.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepository private constructor(
    private val userRemoteDataSource: UserRemoteDataSource
) {
    val userStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }

        userRemoteDataSource.addAuthStateListener(listener)
        awaitClose { userRemoteDataSource.removeAuthStateListener(listener) }
    }

    companion object {
        @Volatile private var instance: UserRepository? = null

        fun getInstance(userRemoteDataSource: UserRemoteDataSource) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userRemoteDataSource).also { instance = it }
            }
    }
}