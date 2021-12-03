package com.hhp227.datemate

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.kortek.myapplication.ui.theme.DateMateTheme

@Composable
fun DateMateApp() {
    ProvideWindowInsets {
        DateMateTheme {
            val viewModel: SignInViewModel = viewModel(factory = SignInViewModelFactory(UserRepository()))

            if (viewModel.signInResult.success) {
                MainScreen()
            } else {
                SignInScreen(viewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DateMateTheme {
        MainScreen()
    }
}