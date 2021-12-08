package com.hhp227.datemate.data

import com.google.firebase.auth.FirebaseAuth

class UserRepository {
    val auth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String, result: (SignInState) -> Unit) {
        result(SignInState.Loading)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result(SignInState.Success)
            }
        }.addOnFailureListener {
            result(SignInState.Failure)
        }
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