package com.hhp227.datemate.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SubFirstScreen(viewModel: SubFirstViewModel = viewModel(), onNavigateUp: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sub First") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Data: ${viewModel.data}", Modifier.clickable { viewModel.signOut() })
        }
    }
}

/*
@Composable
fun UserDetailScreen() {
    ScrollView(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items((0..3).toList()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier
                        .width((LocalConfiguration.current.screenWidthDp - (20 * 2)).dp)
                        .aspectRatio(0.8f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Column {
            Text(
                text = "유저 정보",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Divider()
            ((0..3).toList()).forEach {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "정보: ")
                    Text(text = "정보")
                }
            }
        }
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        ) {
            Text(text = "좋아요")
        }
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        ) {
            Text(text = "연락처 오픈")
        }
    }
    Text(text = "Hello UserDetailScreen")
}

@Composable
@Preview
fun UserDetailScreenPreview() {
    UserDetailScreen()
}
 */