package com.hhp227.datemate.common

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hhp227.datemate.data.datasource.StorageRemoteDataSource
import com.hhp227.datemate.data.datasource.UserLocalDataSource
import com.hhp227.datemate.data.datasource.UserRemoteDataSource
import com.hhp227.datemate.data.repository.StorageRepository
import com.hhp227.datemate.data.repository.UserRepository
import com.hhp227.datemate.ui.auth.forgotpassword.ForgotPasswordViewModel
import com.hhp227.datemate.ui.auth.phoneauth.PhoneAuthViewModel
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

    private fun getStorageRepository(): StorageRepository {
        return StorageRepository.getInstance(getStorageRemoteDataSource())
    }

    private fun getUserRemoteDataSource(): UserRemoteDataSource {
        return UserRemoteDataSource.getInstance(provideFirebaseAuth(), provideFirestore())
    }

    private fun getUserLocalDataSource(context: Context): UserLocalDataSource {
        return UserLocalDataSource.getInstance(context)
    }

    fun getUserRepository(context: Context): UserRepository {
        return UserRepository.getInstance(getUserRemoteDataSource(), getUserLocalDataSource(context), getStorageRepository())
    }

    fun provideSignInViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SignInViewModel(getUserRepository(context)) as T
            }
        }
    }

    fun provideSignUpViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SignUpViewModel(getUserRepository(context)) as T
            }
        }
    }

    fun providePhoneAuthViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PhoneAuthViewModel(getUserRepository(context)) as T
            }
        }
    }

    fun provideProfileSetupViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileSetupViewModel(getUserRepository(context)) as T
            }
        }
    }

    fun provideForgotPasswordViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ForgotPasswordViewModel(getUserRepository(context)) as T
            }
        }
    }

    fun provideDetailViewModelFactory(
        backStackEntry: NavBackStackEntry,
        context: Context
    ): AbstractSavedStateViewModelFactory {
        return object : AbstractSavedStateViewModelFactory(backStackEntry, backStackEntry.arguments) {
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                if (modelClass.isAssignableFrom(SubFirstViewModel::class.java)) {
                    return SubFirstViewModel(
                        getUserRepository(context),
                        handle
                    ) as T
                }
                return super.create(modelClass)
            }
        }
    }
}