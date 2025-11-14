package com.hhp227.datemate.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SubFirstScreen(onNavigateUp: () -> Unit, data: String) {
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
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("Data: $data")
        }
    }
}