package com.hhp227.datemate.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.R
import com.hhp227.datemate.data.PostDetailRepository
import com.hhp227.datemate.model.Comment
import com.hhp227.datemate.util.viewModelProviderFactoryOf
import com.hhp227.datemate.viewmodel.PostDetailViewModel

@Composable
fun PostDetailScreen(
    postKey: String,
    viewModel: PostDetailViewModel = viewModel(factory = viewModelProviderFactoryOf { PostDetailViewModel(PostDetailRepository(), postKey) })
) {
    Column {
        LazyColumn {
            val postState by viewModel.postState
            val commentsState by viewModel.commentsState

            postState.post?.let { post ->
                @OptIn(ExperimentalFoundationApi::class)
                stickyHeader {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_action_account_circle_40),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp, 40.dp)
                            )
                            Text(text = post.author)
                        }
                        Column {
                            Text(text = post.title, maxLines = 1)
                            Text(text = post.body)
                        }
                    }
                    Divider()
                }
            }
            itemsIndexed(commentsState.comments) { i, comment ->
                CommentItem(comment = comment)
            }
        }
        Column {
            Divider()
            Row {
                Text(text = "PostDetailScreen")
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row {
        Image(
            painter = painterResource(id = R.drawable.ic_action_account_circle_40),
            contentDescription = null,
            modifier = Modifier.size(32.dp, 32.dp)
        )
        Column {
            Text(text = comment.author)
            Text(text = comment.text)
        }
    }
}