package com.hhp227.datemate.ui.postdetail

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.CommentRepository
import com.hhp227.datemate.data.repository.PostRepository
import com.hhp227.datemate.data.Comment
import com.hhp227.datemate.data.model.Post
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.lang.Exception

class PostDetailViewModel(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository
): ViewModel() {

}