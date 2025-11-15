package com.hhp227.datemate.ui

import androidx.lifecycle.ViewModel
import com.hhp227.datemate.data.UserRepository

class SignInViewModel internal constructor(
    private val userRepository: UserRepository
) : ViewModel() {
}