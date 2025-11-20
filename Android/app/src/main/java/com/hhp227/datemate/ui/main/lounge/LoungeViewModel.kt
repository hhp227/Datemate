package com.hhp227.datemate.ui.main.lounge

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.PostRepository
import com.hhp227.datemate.data.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.lang.Exception

class LoungeViewModel(
    private val repository: PostRepository
) : ViewModel() {
    val state = mutableStateOf(State())

    private fun getPosts() {
        repository.getPosts().map(::getPostsUseCase).flowOn(Dispatchers.IO).onEach(::onReceive).launchIn(viewModelScope)
    }

    private fun getPostsUseCase(batch: List<Post>): Resource<List<Post>> {
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

    init {
        getPosts()
    }

    data class State(var isLoading: Boolean = false, var posts: List<Post> = emptyList(), var error: String = "")
}