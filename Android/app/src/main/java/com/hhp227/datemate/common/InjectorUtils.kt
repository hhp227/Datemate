package com.hhp227.datemate.common

import com.google.firebase.auth.FirebaseAuth
import com.hhp227.datemate.data.UserRemoteDataSource
import com.hhp227.datemate.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object InjectorUtils {
    private fun getUserRemoteDataSource() = UserRemoteDataSource.getInstance(provideFirebaseAuth())

    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    fun getUserRepository() = UserRepository.getInstance(getUserRemoteDataSource())

    // Temp
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun set(value: Boolean) {
        _isLoggedIn.value = value
    }
}