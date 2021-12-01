package com.hhp227.datemate

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth

class UserRepository {
    val auth = FirebaseAuth.getInstance()

    var isSignedIn: Boolean = auth.currentUser != null

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    @Suppress("UNUSED_PARAMETER")
    fun signUp(email: String, password: String) {

    }
}