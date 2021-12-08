package com.hhp227.datemate

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.viewmodel.LoungeViewModel

@Composable
fun LoungeScreen(viewModel: LoungeViewModel = viewModel()) {
    Text(
        text = "Rounge",
        modifier = Modifier.clickable { viewModel.test() }
    )
}