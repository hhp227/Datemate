package com.hhp227.datemate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hhp227.datemate.data.PostDetailRepository

class PostDetailViewModel(private val repository: PostDetailRepository) {

}

@Suppress("UNCHECKED_CAST")
class PostDetailViewModelFactory(private val repository: PostDetailRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
            return PostDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
