package com.hhp227.datemate.ui

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.insets.ProvideWindowInsets
import com.hhp227.datemate.legacy.ui.MainScreen
import com.kortek.myapplication.ui.theme.DateMateTheme

@Composable
fun DateMateApp() {
    ProvideWindowInsets {
        DateMateTheme {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "main"
            ) {
                composable("main") { MainScreen(navController) }
                composable(
                    route = "sub_first/{data}",
                    arguments = listOf(navArgument("data") { type = NavType.StringType })
                ) { backStackEntry ->
                    val data = backStackEntry.arguments?.getString("data") ?: ""
                    SubFirstScreen(navController, data)
                }
                composable("sub_second") { SubSecondScreen(navController) }
            }
        }
    }
}

/*
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
 */

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DateMateTheme {
        MainScreen()
    }
}