package com.hhp227.datemate.common

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import com.google.firebase.auth.FirebaseAuth
import com.hhp227.datemate.data.datasource.UserRemoteDataSource
import com.hhp227.datemate.data.repository.UserRepository
import com.hhp227.datemate.ui.detail.SubFirstViewModel
import com.hhp227.datemate.ui.auth.signin.SignInViewModel
import com.hhp227.datemate.ui.auth.signup.SignUpViewModel

object InjectorUtils {
    private fun getUserRemoteDataSource() = UserRemoteDataSource.getInstance(provideFirebaseAuth())

    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    fun getUserRepository() = UserRepository.getInstance(getUserRemoteDataSource())

    fun provideSignInViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SignInViewModel(getUserRepository()) as T
            }
        }
    }

    fun provideSignUpViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SignUpViewModel(getUserRepository()) as T
            }
        }
    }

    fun provideDetailViewModelFactory(
        backStackEntry: NavBackStackEntry
    ): AbstractSavedStateViewModelFactory {
        return object : AbstractSavedStateViewModelFactory(backStackEntry, backStackEntry.arguments) {
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                if (modelClass.isAssignableFrom(SubFirstViewModel::class.java)) {
                    return SubFirstViewModel(
                        getUserRepository(),
                        handle
                    ) as T
                }
                return super.create(modelClass)
            }
        }
    }
}