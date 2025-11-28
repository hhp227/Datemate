package com.hhp227.datemate.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.common.asResource
import com.hhp227.datemate.data.datasource.UserRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class UserRepository private constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val storageRepository: StorageRepository
) {
    val signInStateFlow = userRemoteDataSource.userStateFlow
        .map { if (it != null) SignInState.SignIn else SignInState.SignOut }
        .onStart { emit(SignInState.Loading) }

    fun getSignInResultStream(email: String, password: String): Flow<Resource<FirebaseUser>> {
        return userRemoteDataSource.signIn(email, password).asResource()
    }

    fun getSignUpResultStream(email: String, password: String): Flow<Resource<FirebaseUser>> {
        return userRemoteDataSource.signUp(email, password).asResource()
    }

    fun getSignOutResultStream(): Flow<Resource<Boolean>> {
        return userRemoteDataSource.signOut().asResource()
    }

    fun getPasswordResetResultStream(email: String): Flow<Resource<Boolean>> {
        return userRemoteDataSource.sendPasswordResetEmail(email).asResource()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateUserProfile(
        imageUris: List<Uri>,
        fullName: String,
        gender: String,
        birthdayMillis: Long,
        bio: String,
        job: String,
        concurrency: Int = 4,
        retryCount: Int = 0
    ): Flow<Resource<Boolean>> {
        return userRemoteDataSource.userStateFlow
            .filterNotNull()
            .flatMapLatest { user ->
                storageRepository.uploadAllImages(imageUris, user.uid, concurrency, retryCount)
                    .flatMapLatest { uploadedUrls ->
                        userRemoteDataSource.updateUserProfile(
                            userId = user.uid,
                            fullName = fullName,
                            gender = gender,
                            birthdayMillis = birthdayMillis,
                            bio = bio,
                            job = job,
                            profileImageUrls = uploadedUrls
                        )
                    }
            }
            .asResource()
    }

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