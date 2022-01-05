package com.hhp227.datemate.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
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
    private val postKey: String
    ): ViewModel() {
    val postState = mutableStateOf(PostState())

    val commentsState = mutableStateOf(CommentsState())

    val isMyPost = mutableStateOf(false)

    var message: String = ""

    private fun getPost(key: String) {
        repository.getPost(key).map(::getPostUseCase).onEach(::onReceive).launchIn(viewModelScope)
    }

    private fun getComments(key: String) {
        repository.getComments(key).map(::getCommentsUseCase).onEach(::onReceive).launchIn(viewModelScope)
    }

    private fun getUserPostKeys() {
        repository.getUserPostKeys().onEach(::onReceive).launchIn(viewModelScope)
    }

    private fun getPostUseCase(post: Post): Resource<Post> {
        return try {
            Resource.Success(data = post)
        } catch (e: Exception) {
            Resource.Error(message = e.localizedMessage ?: "An unexpected error occured")
        }
    }

    private fun getCommentsUseCase(comments: List<Comment>): Resource<List<Comment>> {
        return try {
            Resource.Success(data = comments)
        } catch (e: Exception) {
            Resource.Error(message = e.localizedMessage ?: "An unexpected error occured")
        }
    }

    @JvmName("onReceivePost")
    private fun onReceive(result: Resource<Post>) {
        when (result) {
            is Resource.Success -> {
                this.postState.value = PostState(post = result.data)
            }
            is Resource.Error -> {
                this.postState.value = PostState(error = result.message ?: "An unexpected error occured")
            }
            is Resource.Loading -> {
                this.postState.value = PostState(isLoading = true)
            }
        }
    }

    @JvmName("onReceiveComment")
    private fun onReceive(result: Resource<List<Comment>>) {
        when (result) {
            is Resource.Success -> {
                this.commentsState.value = CommentsState(comments = result.data ?: emptyList())
            }
            is Resource.Error -> {
                this.commentsState.value = CommentsState(error = result.message ?: "An unexpected error occured")
            }
            is Resource.Loading -> {
                this.commentsState.value = CommentsState(isLoading = true)
            }
        }
    }

    private fun onReceive(keys: List<String>) {
        isMyPost.value = keys.contains(postKey)
    }

    init {
        getPost(postKey)
        getComments(postKey)
        getUserPostKeys()
    }

    data class PostState(
        var isLoading: Boolean = false,
        var post: Post? = null,
        var error: String = ""
    )

    data class CommentsState(
        var isLoading: Boolean = false,
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