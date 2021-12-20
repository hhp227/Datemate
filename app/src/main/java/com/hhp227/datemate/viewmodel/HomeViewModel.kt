package com.hhp227.datemate.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel : ViewModel() {
    fun test() {
        FirebaseAuth.getInstance().signOut()
    }
}