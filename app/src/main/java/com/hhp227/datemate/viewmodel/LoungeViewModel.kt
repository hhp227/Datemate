package com.hhp227.datemate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.hhp227.datemate.data.LoungeRepository
import com.hhp227.datemate.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class LoungeViewModel(
    private val repository: LoungeRepository
) : ViewModel() {
    fun test() {
        Log.e("TEST", "LoungeViewModel test ${repository.test()}")
    }

    fun getPosts() = repository.getResponseFromRealtimeDatabaseUsingFlow()
}