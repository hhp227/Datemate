package com.hhp227.datemate.data.repository

import android.app.Activity
import com.google.firebase.auth.FirebaseUser
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.common.asResource
import com.hhp227.datemate.data.datasource.UserLocalDataSource
import com.hhp227.datemate.data.datasource.UserRemoteDataSource
import com.hhp227.datemate.data.model.UserCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class UserRepository private constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
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
        return flow {
            try {
                val user = userRemoteDataSource.signIn(email, password)

                emit(Resource.Success(user))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "로그인 실패"))
            }
        }
            .onStart { emit(Resource.Loading()) }
    }

    fun getSignUpResultStream(email: String, password: String): Flow<Resource<FirebaseUser>> {
        return flow {
            try {
                val user = userRemoteDataSource.signUp(email, password)

                emit(Resource.Success(user))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "회원가입 실패"))
            }
        }
            .onStart { emit(Resource.Loading()) }
    }

    fun getSignOutResultStream(): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Success(userRemoteDataSource.signOut()))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "로그아웃 실패"))
            }
        }
            .onStart { emit(Resource.Loading()) }
    }

    fun getPasswordResetResultStream(email: String): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Success(userRemoteDataSource.sendPasswordResetEmail(email)))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: ""))
            }
        }
            .onStart { emit(Resource.Loading()) }
    }

    fun createUserProfileResultStream(userId: String, email: String?): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Success(userRemoteDataSource.createUserProfile(userId, email)))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "프로필 생성 실패"))
            }
        }
            .onStart { emit(Resource.Loading()) }
    }

    suspend fun storeUserProfile(userCache: UserCache?) {
        userLocalDataSource.storeUser(userCache)
    }

    fun updateUserProfile(name: String, imageUrl: List<String>): Flow<Resource<Boolean>> {
        return flow {
            try {
                val result = userRemoteDataSource.updateUserProfile(
                    name,
                    imageUrl.firstOrNull()
                )

                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "프로필 업데이트 실패"))
            }
        }
            .onStart { emit(Resource.Loading()) }
    }

    fun sendOtp(phoneNumber: String, activityProvider: () -> Activity): Flow<Resource<String>> {
        return userRemoteDataSource.sendOtp(phoneNumber, activityProvider).asResource()
    }

    fun verifyOtp(verificationId: String, code: String): Flow<Resource<Boolean>> {
        return flow {
            try {
                userRemoteDataSource.verifyOtp(verificationId, code)
                emit(Resource.Success(true))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "OTP 검증 실패"))
            }
        }
            .onStart { emit(Resource.Loading()) }
    }

    enum class SignInState {
        SignIn, SignOut, Loading
    }

    companion object {
        @Volatile private var instance: UserRepository? = null

        fun getInstance(
            userRemoteDataSource: UserRemoteDataSource,
            userLocalDataSource: UserLocalDataSource
        ) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userRemoteDataSource, userLocalDataSource).also { instance = it }
            }
    }
}