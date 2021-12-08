package com.hhp227.datemate

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.hhp227.datemate.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kortek.myapplication.ui.theme.DateMateTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    Text("Home")
}

@Preview(name = "Home in light theme")
@Composable
fun HomePreview() {
    DateMateTheme {
        HomeScreen()
    }
}