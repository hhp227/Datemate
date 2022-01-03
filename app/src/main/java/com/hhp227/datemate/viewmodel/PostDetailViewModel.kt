package com.hhp227.datemate.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.PostDetailRepository
import com.hhp227.datemate.model.Comment
import com.hhp227.datemate.model.Post
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.lang.Exception

class PostDetailViewModel(
    private val repository: PostDetailRepository,
    key: String
    ): ViewModel() {
    val state = mutableStateOf(State())

    var message: String = ""

    private fun getPost(key: String) {
        repository.getPost(key).map(::getPostUseCase).onEach(::onReceive).launchIn(viewModelScope)
    }

    private fun getPostUseCase(post: Post): Resource<Post> {
        return try {
            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occured")
        }
    }

    private fun onReceive(result: Resource<Post>) {
        when (result) {
            is Resource.Success -> {
                this.state.value = State(post = result.data)
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
        getPost(key)
    }

    data class State(
        var isLoading: Boolean = false,
        var post: Post? = null,
        var comments: List<Comment> = emptyList(),
        var error: String = ""
    )
}

/*@Suppress("UNCHECKED_CAST")
class PostDetailViewModelFactory(private val repository: PostDetailRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
            return PostDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
*/