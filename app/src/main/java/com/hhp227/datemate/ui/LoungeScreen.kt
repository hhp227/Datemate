package com.hhp227.datemate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.hhp227.datemate.R
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.LoungeRepository
import com.hhp227.datemate.model.Post
import com.hhp227.datemate.util.viewModelProviderFactoryOf
import com.hhp227.datemate.viewmodel.LoungeViewModel
import com.kortek.myapplication.ui.theme.DateMateTheme

@Composable
fun LoungeScreen(
    viewModel: LoungeViewModel = viewModel(factory = viewModelProviderFactoryOf { LoungeViewModel(LoungeRepository()) }),
    onNavigate: () -> Unit
) {
    val state by viewModel.state

    Box {
        when {
            state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            state.error.isNotBlank() -> Text(
                text = state.error,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            items(state.posts) { post ->
                PostItem(post = post, onItemClick = onNavigate)
                Divider()
            }
        }
    }
}

@Composable
fun PostItem(post: Post, onItemClick: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onItemClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_action_account_circle_40),
                contentDescription = null,
                modifier = Modifier.size(40.dp, 40.dp)
            )
            Text(text = post.author)
        }
        Column {
            Text(text = post.title)
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
