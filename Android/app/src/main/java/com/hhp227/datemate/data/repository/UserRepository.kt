package com.hhp227.datemate.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.datasource.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await

class UserRepository private constructor(
    private val userRemoteDataSource: UserRemoteDataSource
) {
    val signInStateFlow = userRemoteDataSource.userStateFlow
        .map { if (it != null) SignInState.SignIn else SignInState.SignOut }
        .onStart { emit(SignInState.Loading) }

    fun getSignInResultStream(email: String, password: String): Flow<Resource<FirebaseUser>> {
        return userRemoteDataSource.signIn(email, password)
    }

    fun getSignUpResultStream(email: String, password: String): Flow<Resource<FirebaseUser>> {
        return userRemoteDataSource.signUp(email, password)
    }

    fun getSignOutResultStream(): Flow<Resource<Boolean>> {
        return userRemoteDataSource.signOut()
    }

    fun getPasswordResetResultStream(email: String): Flow<Resource<Boolean>> {
        return userRemoteDataSource.sendPasswordResetEmail(email)
    }

    fun updateUserProfile(
        imageUris: List<Uri>,
        nickname: String
    ): Flow<Resource<Boolean>> = flow {
        /*val userId = userRemoteDataSource.firebaseAuth.currentUser?.uid
            ?: return@flow emit(Resource.Error("로그인이 필요합니다."))

        val uploadedImageUrls = mutableListOf<String>()

        // 1. 이미지 업로드: 모든 이미지를 순회하며 개별 업로드
        imageUris.forEachIndexed { index, uri ->
            // 고유한 파일명과 경로를 생성합니다.
            val path = "users/$userId/gallery_${index}_${System.currentTimeMillis()}.jpg"

            // Data Source 호출
            when (val imageResult = userRemoteDataSource.uploadProfileImage(uri, path)) {
                is Resource.Success -> uploadedImageUrls.add(imageResult.data)
                is Resource.Error -> return@flow emit(Resource.Error("이미지 업로드 실패: ${imageResult.message}"))
                else -> {}
            }
        }

        // 2. Auth 및 Firestore 업데이트: 닉네임과 URL 목록을 전달
        // Data Source 호출
        when (val updateResult = userRemoteDataSource.updateUserProfile(userId, nickname, uploadedImageUrls)) {
            is Resource.Success -> emit(Resource.Success(true))
            is Resource.Error -> emit(Resource.Error(updateResult.message))
            else -> {}
        }*/
        emit(Resource.Success(true))
    }
        //.onStart { emit(Resource.Loading()) }
        //.catch { e -> emit(Resource.Error(e.message ?: "알 수 없는 프로필 업데이트 오류")) }

    enum class SignInState {
        SignIn, SignOut, Loading
    }

    companion object {
        @Volatile private var instance: UserRepository? = null

        fun getInstance(userRemoteDataSource: UserRemoteDataSource) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userRemoteDataSource).also { instance = it }
            }
    }
}