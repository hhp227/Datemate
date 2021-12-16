package com.hhp227.datemate.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.data.LoungeRepository
import com.hhp227.datemate.model.Post
import kotlinx.coroutines.flow.*

class LoungeViewModel(
    private val repository: LoungeRepository
) : ViewModel() {
    val posts = mutableStateOf(emptyList<Post>())

    private fun onReceive(list: List<Post>) {
        this.posts.value = list
    }

    fun test() {
        Log.e("TEST", "LoungeViewModel test ${repository.test()}")
    }

    private fun getPosts() {
        repository.getResponseFromRealtimeDatabaseUsingFlow().onEach(::onReceive).launchIn(viewModelScope)
    }

    init {
        getPosts()
    }
}


