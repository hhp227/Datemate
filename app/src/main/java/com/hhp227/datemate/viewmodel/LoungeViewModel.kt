package com.hhp227.datemate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.hhp227.datemate.data.LoungeRepository

class LoungeViewModel(
    private val repository: LoungeRepository
) : ViewModel() {


    fun test() {
        Log.e("TEST", "LoungeViewModel test ${repository.test()}")
    }

    fun test2() = repository.getResponseFromRealtimeDatabaseUsingFlow()
}