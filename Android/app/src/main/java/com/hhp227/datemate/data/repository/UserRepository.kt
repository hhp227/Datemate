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
    private val userRemoteDataSource: UserRemoteDataSource,
    private val storageRepository: StorageRepository
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
        fullName: String,          // 🆕
        gender: String,            // 🆕
        birthdayMillis: Long,      // 🆕
        bio: String,               // 🆕
        job: String                // 🆕
    ): Flow<Resource<Boolean>> = flow {
        // 1. 현재 로그인된 사용자 UID 확인
        /*val userId = userRemoteDataSource.firebaseAuth.currentUser?.uid
            ?: return@flow emit(Resource.Error("로그인이 필요합니다."))

        val uploadedImageUrls = mutableListOf<String>()

        // 2. 이미지 업로드: 모든 이미지를 순회하며 개별 업로드
        imageUris.forEachIndexed { index, uri ->
            when (val imageResult = storageRepository.uploadProfileImage(uri, userId, index)) { // StorageRepository 사용
                is Resource.Success -> uploadedImageUrls.add(imageResult.data)
                is Resource.Error -> return@flow emit(Resource.Error("이미지 업로드 실패: ${imageResult.message}"))
                else -> {}
            }
        }

        // 3. Auth 및 Firestore 업데이트: 모든 상세 정보와 URL 목록을 전달
        when (val updateResult = userRemoteDataSource.updateUserProfile(
            userId = userId,
            fullName = fullName,
            gender = gender,
            birthdayMillis = birthdayMillis,
            bio = bio,
            job = job,
            profileImageUrls = uploadedImageUrls
        )) {
            is Resource.Success -> */emit(Resource.Success(true))
            /*is Resource.Error -> emit(Resource.Error(updateResult.message))
            else -> {}
        }*/
    }
        //.onStart { emit(Resource.Loading()) } // 🌟 로딩 시작 활성화
        //.catch { e -> emit(Resource.Error(e.message ?: "알 수 없는 프로필 업데이트 오류")) } // 🌟 에러 처리 활성화

    enum class SignInState {
        SignIn, SignOut, Loading
    }

    companion object {
        @Volatile private var instance: UserRepository? = null

        fun getInstance(userRemoteDataSource: UserRemoteDataSource, storageRepository: StorageRepository) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userRemoteDataSource, storageRepository).also { instance = it }
            }
    }
}