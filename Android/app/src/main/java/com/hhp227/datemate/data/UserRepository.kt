package com.hhp227.datemate.data

import com.google.firebase.auth.FirebaseUser
import com.hhp227.datemate.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class UserRepository private constructor(
    private val userRemoteDataSource: UserRemoteDataSource
) {
    val loginStateFlow = userRemoteDataSource.userStateFlow
        .map { if (it != null) LoginState.Login else LoginState.Logout }
        .onStart { emit(LoginState.Loading) }

    fun getSignInResultStream(email: String, password: String): Flow<Resource<FirebaseUser>> {
        return userRemoteDataSource.signIn(email, password)
    }

    fun getSignOutResultStream(): Flow<Resource<Boolean>> {
        return userRemoteDataSource.signOut()
    }

    enum class LoginState {
        Login, Logout, Loading
    }

    companion object {
        @Volatile private var instance: UserRepository? = null

        fun getInstance(userRemoteDataSource: UserRemoteDataSource) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userRemoteDataSource).also { instance = it }
            }
    }
}