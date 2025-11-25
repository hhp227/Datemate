package com.hhp227.datemate.common

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hhp227.datemate.data.datasource.StorageRemoteDataSource
import com.hhp227.datemate.data.datasource.UserRemoteDataSource
import com.hhp227.datemate.data.repository.StorageRepository
import com.hhp227.datemate.data.repository.UserRepository
import com.hhp227.datemate.ui.auth.forgotpassword.ForgotPasswordViewModel
import com.hhp227.datemate.ui.auth.profilesetup.ProfileSetupViewModel
import com.hhp227.datemate.ui.detail.SubFirstViewModel
import com.hhp227.datemate.ui.auth.signin.SignInViewModel
import com.hhp227.datemate.ui.auth.signup.SignUpViewModel

object InjectorUtils {
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    private fun getStorageRemoteDataSource(): StorageRemoteDataSource {
        return StorageRemoteDataSource.getInstance(provideStorage())
    }

    // 🆕 StorageRepository 싱글톤 제공
    private fun getStorageRepository(): StorageRepository {
        return StorageRepository.getInstance(getStorageRemoteDataSource())
    }

    private fun getUserRemoteDataSource(): UserRemoteDataSource {
        return UserRemoteDataSource.getInstance(provideFirebaseAuth(), provideFirestore())
    }

    fun getUserRepository(): UserRepository {
        return UserRepository.getInstance(getUserRemoteDataSource(), getStorageRepository())
    }

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

    fun provideProfileSetupViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileSetupViewModel(getUserRepository()) as T
            }
        }
    }

    fun provideForgotPasswordViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ForgotPasswordViewModel(getUserRepository()) as T
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