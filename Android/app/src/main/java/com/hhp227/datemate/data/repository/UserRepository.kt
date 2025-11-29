package com.hhp227.datemate.data.repository

import android.app.Activity
import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.common.asResource
import com.hhp227.datemate.data.datasource.UserLocalDataSource
import com.hhp227.datemate.data.datasource.UserRemoteDataSource
import com.hhp227.datemate.data.model.Profile
import com.hhp227.datemate.data.model.UserCache
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class UserRepository private constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val storageRepository: StorageRepository
) {
    val remoteUserStateFlow = userRemoteDataSource.userStateFlow

    val localUserStateFlow = userLocalDataSource.userFlow

    val signInStateFlow: Flow<SignInState> = localUserStateFlow
        .map { userCache ->
            if (userCache != null) {
                SignInState.SignIn
            } else {
                SignInState.SignOut
            }
        }
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

    fun fetchUserProfile(userId: String): Flow<Resource<Profile?>> {
        return userRemoteDataSource.fetchUserProfile(userId).asResource()
    }

    fun createUserProfile(userId: String, email: String?): Flow<Resource<Boolean>> {
        return userRemoteDataSource.createUserProfile(userId, email).asResource()
    }

    suspend fun storeUserProfile(userCache: UserCache) {
        userLocalDataSource.storeUser(userCache)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateUserProfile(
        imageUris: List<Uri>,
        name: String,
        gender: String,
        birthdayMillis: Long,
        bio: String,
        job: String,
        concurrency: Int = 4,
        retryCount: Int = 1
    ): Flow<Resource<String>> {
        return userRemoteDataSource.userStateFlow
            .filterNotNull()
            .flatMapLatest { user ->
                storageRepository.uploadAllImages(imageUris, user.uid, concurrency, retryCount)
                    .flatMapLatest { uploadedUrls ->
                        userRemoteDataSource.updateUserProfile(
                            userId = user.uid,
                            name = name,
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

    fun sendOtp(phoneNumber: String, activityProvider: () -> Activity): Flow<Resource<String>> {
        return userRemoteDataSource.sendOtp(phoneNumber, activityProvider).asResource()
    }

    fun verifyOtp(verificationId: String, code: String): Flow<Resource<Unit>> {
        return userRemoteDataSource.verifyOtp(verificationId, code).asResource()
    }

    enum class SignInState {
        SignIn, SignOut, Loading
    }

    companion object {
        @Volatile private var instance: UserRepository? = null

        fun getInstance(
            userRemoteDataSource: UserRemoteDataSource,
            userLocalDataSource: UserLocalDataSource,
            storageRepository: StorageRepository
        ) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userRemoteDataSource, userLocalDataSource, storageRepository).also { instance = it }
            }
    }
}