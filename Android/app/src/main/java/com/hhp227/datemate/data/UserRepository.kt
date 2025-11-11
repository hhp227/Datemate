package com.hhp227.datemate.data

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.hhp227.datemate.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class UserRepository {
    val auth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String): Flow<AuthResult?> {
        val mutableStateFlow = MutableStateFlow<AuthResult?>(null)

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                mutableStateFlow.value = task.result
            } else {
                task.exception
            }
        }
        return mutableStateFlow
    }

    fun signOut() {
        auth.signOut()
    }

    @Suppress("UNUSED_PARAMETER")
    fun signUp(email: String, password: String) {

    }

    fun getCurrentUser() = auth.currentUser
}

enum class SignInState {
    Success, Loading, Failure
}