package com.hhp227.datemate

import android.util.Log
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    fun signIn(email: String, password: String) {
        Log.e("TEST", "$email, $password")
    }
}