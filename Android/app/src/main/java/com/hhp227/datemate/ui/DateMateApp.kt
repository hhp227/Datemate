package com.hhp227.datemate.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.insets.ProvideWindowInsets
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.legacy.ui.MainScreen
import com.kortek.myapplication.ui.theme.DateMateTheme

@Composable
fun DateMateApp() {
    ProvideWindowInsets {
        DateMateTheme {
            val navController = rememberNavController()
            val isLoggedIn by InjectorUtils.isLoggedIn.collectAsState()

            if (isLoggedIn) {
                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainScreen(
                            onNavigateToSubFirst = { data -> navController.navigate("sub_first/$data") },
                            onNavigateToSubSecond = { navController.navigate("sub_second") }
                        )
                    }
                    composable(
                        route = "sub_first/{data}",
                        arguments = listOf(navArgument("data") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val data = backStackEntry.arguments?.getString("data") ?: ""
                        SubFirstScreen(onNavigateUp = { navController.navigateUp() }, data)
                    }
                    composable("sub_second") { SubSecondScreen(onNavigateUp = { navController.navigateUp() }) }
                }
            } else {
                NavHost(
                    navController = navController,
                    startDestination = "sign_in"
                ) {
                    composable("sign_in") {
                        SignInScreen(
                            onSignUp = { navController.navigate("sign_up") })
                    }
                    composable("sign_up") {
                        SignUpScreen()
                    }
                }
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