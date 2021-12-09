package com.hhp227.datemate

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.data.LoungeRepository
import com.hhp227.datemate.util.viewModelProviderFactoryOf
import com.hhp227.datemate.viewmodel.LoungeViewModel

@Composable
fun LoungeScreen(viewModel: LoungeViewModel = viewModel(factory = viewModelProviderFactoryOf { LoungeViewModel(LoungeRepository()) })) {
    val temp = viewModel.test2().collectAsState(initial = emptyList())

    Text(
        text = "Rounge",
        modifier = Modifier.clickable {
            viewModel.test()
            Log.e("TEST", "temp: $temp")
        }
    )
}