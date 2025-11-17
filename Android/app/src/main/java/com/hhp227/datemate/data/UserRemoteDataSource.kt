package com.hhp227.datemate.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hhp227.datemate.common.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await

class UserRemoteDataSource private constructor(
    private val firebaseAuth: FirebaseAuth
) {
    val userStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }

        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    fun signIn(email: String, password: String): Flow<Resource<FirebaseUser>> = flow {
        try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()
            val user = result.user

            if (user != null) {
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error("로그인 실패"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("로그인 오류: ${e.message}"))
        }
    }
        .onStart { emit(Resource.Loading()) }

    fun signOut(): Flow<Resource<Boolean>> = flow {
        try {
            firebaseAuth.signOut()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: ""))
        }
    }
        .onStart { emit(Resource.Loading()) }

    suspend fun signUp(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    companion object {
        @Volatile
        private var instance: UserRemoteDataSource? = null
        fun getInstance(auth: FirebaseAuth) =
            instance ?: synchronized(this) { instance ?: UserRemoteDataSource(auth).also { instance = it } }
    }
}