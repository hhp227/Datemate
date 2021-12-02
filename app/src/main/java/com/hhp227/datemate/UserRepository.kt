package com.hhp227.datemate

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class UserRepository {
    val auth = FirebaseAuth.getInstance()

    var isSignedIn: Boolean = auth.currentUser == null

    fun signIn(email: String, password: String, result: (SignInStatus) -> Unit) {
        result(SignInStatus.Loading)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isSignedIn = true
                Log.e("TEST", "LoginSuccess")
                result(SignInStatus.Success)
            }
        }.addOnFailureListener {
            result(SignInStatus.Failure)
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

enum class SignInStatus {
    Success, Loading, Failure
}