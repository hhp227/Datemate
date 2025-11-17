package com.hhp227.datemate.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.data.UserRepository
import com.hhp227.datemate.common.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SubFirstViewModel(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val data: String? = savedStateHandle["data"]

    fun signOut() {
        viewModelScope.launch {
            userRepository.getSignOutResultStream().collectLatest { result ->
                when (result) {
                    is Resource.Error<*> -> {
                    }
                    is Resource.Loading<*> -> {}
                    is Resource.Success<*> -> {}
                }
            }
        }
    }
}