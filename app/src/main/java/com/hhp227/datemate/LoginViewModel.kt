package com.hhp227.datemate

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    val signedIn = mutableStateOf(false)

    fun signIn(email: String, password: String) {
        Log.e("TEST", "$email, $password")
    }
}