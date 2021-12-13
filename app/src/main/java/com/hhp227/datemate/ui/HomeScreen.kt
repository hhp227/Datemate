package com.hhp227.datemate.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hhp227.datemate.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kortek.myapplication.ui.theme.DateMateTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    Text(
        text = "Home",
        modifier = Modifier.clickable { viewModel.test() }
    )
}

@Preview(name = "Home in light theme")
@Composable
fun HomePreview() {
    DateMateTheme {
        HomeScreen()
    }
}