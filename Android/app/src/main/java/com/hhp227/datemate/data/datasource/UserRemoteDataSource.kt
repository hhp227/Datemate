package com.hhp227.datemate.data.datasource

import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hhp227.datemate.common.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await

class UserRemoteDataSource private constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val userStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }

        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    fun signIn(email: String, password: String): Flow<FirebaseUser> = flow {
        val result = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()
        val user = result.user ?: throw Exception("로그인 실패")
        emit(user)
    }

    fun signOut(): Flow<Boolean> = flow {
        firebaseAuth.signOut()
        emit(true)
    }

    fun signUp(email: String, password: String): Flow<FirebaseUser> = flow {
        val result = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
        val user = result.user ?: throw Exception("회원가입 실패")
        emit(user)
    }

    fun updateUserProfile(
        userId: String,
        fullName: String,
        gender: String,
        birthdayMillis: Long,
        bio: String,
        job: String,
        profileImageUrls: List<String>?
    ): Flow<Boolean> = flow {
        val user = firebaseAuth.currentUser ?: throw Exception("로그인 정보 없음")
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .setPhotoUri(profileImageUrls?.firstOrNull()?.toUri())
            .build()

        user.updateProfile(profileUpdates).await()

        val userDocument = firestore.collection("users").document(userId)
        val userData = mapOf(
            "userId" to userId,
            "fullName" to fullName,
            "gender" to gender,
            "birthdayMillis" to birthdayMillis,
            "bio" to bio,
            "job" to job,
            "profileImageUrls" to (profileImageUrls ?: emptyList()),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        userDocument.set(userData, SetOptions.merge()).await()
        emit(true)
    }

    fun sendPasswordResetEmail(email: String): Flow<Boolean> = flow {
        firebaseAuth.sendPasswordResetEmail(email).await()
        emit(true)
    }

    companion object {
        @Volatile
        private var instance: UserRemoteDataSource? = null
        fun getInstance(
            auth: FirebaseAuth,
            firestore: FirebaseFirestore
        ) =
            instance ?: synchronized(this) {
                instance ?: UserRemoteDataSource(auth, firestore).also { instance = it }
            }
    }
}