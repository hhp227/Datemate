package com.hhp227.datemate.data.datasource

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
    private val firebaseAuth: FirebaseAuth,
    //private val firestore: FirebaseFirestore,
    //private val storage: FirebaseStorage
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

    /*suspend fun uploadProfileImage(imageUri: Uri, path: String): Resource<String> = try {
        val storageRef = storage.reference.child(path)

        // 업로드 후 성공할 때까지 대기
        val uploadTask = storageRef.putFile(imageUri).await()

        // 다운로드 URL 획득
        val downloadUrl = storageRef.downloadUrl.await().toString()

        Resource.Success(downloadUrl)
    } catch (e: Exception) {
        Resource.Error("이미지 업로드 실패: ${e.message}")
    }*/

    suspend fun updateUserProfile(
        userId: String,
        nickname: String,
        profileImageUrls: List<String>?
    ): Resource<Boolean> = try {
        /*val user = firebaseAuth.currentUser

        // 1. Firebase Authentication 업데이트 (첫 번째 이미지만 대표 URL로 사용)
        val firstImageUrl = profileImageUrls?.firstOrNull()
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(nickname)
            .setPhotoUri(firstImageUrl?.toUri())
            .build()

        user.updateProfile(profileUpdates).await()

        // 2. Firestore에 모든 이미지 URL 저장
        val userDocument = firestore.collection("users").document(userId)
        val userData = hashMapOf(
            "nickname" to nickname,
            "profileImageUrls" to (profileImageUrls ?: emptyList()), // 리스트 전체를 저장
            "updatedAt" to FieldValue.serverTimestamp()
        )
        userDocument.set(userData, SetOptions.merge()).await()*/

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
        fun getInstance(auth: FirebaseAuth) =
            instance ?: synchronized(this) { instance ?: UserRemoteDataSource(auth).also { instance = it } }
    }
}