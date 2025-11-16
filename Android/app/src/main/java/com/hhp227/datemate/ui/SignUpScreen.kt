package com.hhp227.datemate.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hhp227.datemate.common.InjectorUtils

@Composable
fun SignUpScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Hello")
        Button(onClick = {
            /* viewModel.signUp() */
            InjectorUtils.set(true)
        }) { Text("Complete Register") }
    }
}