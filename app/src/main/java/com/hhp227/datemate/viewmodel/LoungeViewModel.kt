package com.hhp227.datemate.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.LoungeRepository
import com.hhp227.datemate.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.lang.Exception

class LoungeViewModel(
    private val repository: LoungeRepository
) : ViewModel() {
    val state = mutableStateOf(State())

    private fun getPosts() {
        repository.getPosts().map(::postUseCase).flowOn(Dispatchers.IO).onEach(::onReceive).launchIn(viewModelScope)
    }

    private fun postUseCase(batch: List<Post>): Resource<List<Post>> {
        return try {
            Resource.Success(batch)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occured")
        }
    }

    private fun onReceive(result: Resource<List<Post>>) {
        when (result) {
            is Resource.Success -> {
                this.state.value = State(posts = result.data ?: emptyList())
            }
            is Resource.Error -> {
                this.state.value = State(error = result.message ?: "An unexpected error occured")
            }
            is Resource.Loading -> {
                this.state.value = State(isLoading = true)
            }
        }
    }

    fun test() {
        Log.e("TEST", "LoungeViewModel test ${repository.test()}")
    }

    init {
        getPosts()
    }

    data class State(var isLoading: Boolean = false, var posts: List<Post> = emptyList(), var error: String = "")
}


