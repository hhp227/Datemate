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

    fun signUp(email: String, password: String): Flow<Resource<FirebaseUser>> = flow {
        try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()
            val user = result.user

            if (user != null) {
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error("회원가입 실패: 알 수 없는 오류"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("회원가입 오류: ${e.message}"))
        }
    }
        .onStart { emit(Resource.Loading()) }

    suspend fun updateUserProfile(
        userId: String,
        fullName: String,
        gender: String,
        birthdayMillis: Long,
        bio: String,
        job: String,
        profileImageUrls: List<String>?
    ): Resource<Boolean> = try {
        val user = firebaseAuth.currentUser
        val firstImageUrl = profileImageUrls?.firstOrNull()
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .setPhotoUri(firstImageUrl?.toUri())
            .build()

        user?.updateProfile(profileUpdates)?.await()

        val userDocument = firestore.collection("users").document(userId)

        val userData = mapOf(
            "userId" to userId,
            "fullName" to fullName,
            "gender" to gender,
            "birthdayMillis" to birthdayMillis,
            "bio" to bio,
            "job" to job,
            "profileImageUrls" to (profileImageUrls ?: emptyList()),
            "updatedAt" to FieldValue.serverTimestamp(),
            "createdAt" to FieldValue.serverTimestamp()
        )

        // SetOptions.merge()를 사용하여 기존 필드는 유지하고 필요한 필드만 업데이트
        userDocument.set(userData, SetOptions.merge()).await()

        Resource.Success(true)
    } catch (e: Exception) {
        Resource.Error("프로필 업데이트 오류: ${e.message}")
    }

    fun sendPasswordResetEmail(email: String): Flow<Resource<Boolean>> = flow {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error("비밀번호 재설정 이메일 전송 실패: ${e.message}"))
        }
    }
        .onStart { emit(Resource.Loading()) }

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