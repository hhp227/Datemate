package com.hhp227.datemate

import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth

class UserRepository {
    val dataStore = preferencesDataStore("user")

    val auth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String, result: (SignInStatus) -> Unit) {
        result(SignInStatus.Loading)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
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