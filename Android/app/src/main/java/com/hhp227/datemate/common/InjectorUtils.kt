package com.hhp227.datemate.common

import com.google.firebase.auth.FirebaseAuth
import com.hhp227.datemate.data.UserRemoteDataSource
import com.hhp227.datemate.data.UserRepository

object InjectorUtils {
    private fun getUserRemoteDataSource() = UserRemoteDataSource.getInstance(provideFirebaseAuth())

    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    fun getUserRepository() = UserRepository.getInstance(getUserRemoteDataSource())
}