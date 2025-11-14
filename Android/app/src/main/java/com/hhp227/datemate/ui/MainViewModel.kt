package com.hhp227.datemate.ui

import androidx.lifecycle.ViewModel
import com.hhp227.datemate.common.InjectorUtils

class MainViewModel : ViewModel() {
    val isLoggedIn = InjectorUtils.isLoggedIn
}