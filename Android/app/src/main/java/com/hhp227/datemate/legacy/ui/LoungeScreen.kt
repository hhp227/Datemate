package com.hhp227.datemate.legacy.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.R
import com.hhp227.datemate.legacy.data.PostRepository
import com.hhp227.datemate.legacy.model.Post
import com.hhp227.datemate.util.viewModelProviderFactoryOf
import com.hhp227.datemate.legacy.viewmodel.LoungeViewModel
import com.hhp227.datemate.ui.theme.DateMateTheme

@Composable
fun LoungeScreen(
    modifier: Modifier = Modifier,
    viewModel: LoungeViewModel = viewModel(factory = viewModelProviderFactoryOf { LoungeViewModel(PostRepository()) }),
    onNavigate: (String) -> Unit
) {
    val state by viewModel.state

    Box {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            itemsIndexed(state.posts) { i, post ->
                PostItem(post = post, onItemClick = onNavigate)
                Divider()
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.error.isNotBlank()) {
            Text(
                text = state.error,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun PostItem(post: Post, onItemClick: (String) -> Unit) {
    Column(modifier = Modifier.clickable(onClick = { onItemClick(post.key) })) {
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
}

@Preview(name = "Lounge light theme")
@Composable
fun LoungePreview() {
    DateMateTheme {
        LoungeScreen {}
    }
}

@Preview(name = "Lounge dark theme")
@Composable
fun LoungePreviewDark() {
    DateMateTheme(darkTheme = true) {
        LoungeScreen {}
    }
}
