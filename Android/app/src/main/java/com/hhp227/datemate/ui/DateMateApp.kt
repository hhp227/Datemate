package com.hhp227.datemate.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.insets.ProvideWindowInsets
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.data.repository.UserRepository.SignInState
import com.hhp227.datemate.ui.auth.profilesetup.ProfileSetupScreen
import com.hhp227.datemate.ui.detail.SubFirstScreen
import com.hhp227.datemate.ui.main.MainScreen
import com.hhp227.datemate.ui.postdetail.PostDetailScreen
import com.hhp227.datemate.ui.auth.signin.SignInScreen
import com.hhp227.datemate.ui.auth.signup.SignUpScreen
import com.hhp227.datemate.ui.theme.DateMateTheme

@Composable
fun DateMateApp() {
    ProvideWindowInsets {
        DateMateTheme {
            val signInState by InjectorUtils.getUserRepository().signInStateFlow.collectAsState(SignInState.Loading)

            when (signInState) {
                SignInState.SignIn -> AppNavHost()
                SignInState.SignOut -> SignInNavHost()
                SignInState.Loading -> Unit
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
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
            arguments = listOf(
                navArgument("data") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            SubFirstScreen(
                viewModel = viewModel(factory = InjectorUtils.provideDetailViewModelFactory(backStackEntry)),
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable("sub_second") { PostDetailScreen(onNavigateUp = { navController.navigateUp() }) }
    }
}

@Composable
fun SignInNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "sign_in"
    ) {
        composable("sign_in") {
            SignInScreen(onSignUp = { navController.navigate("sign_up") })
        }
        composable("sign_up") {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("profile_setup") {
                        popUpTo("sign_in") { inclusive = true }
                    }
                },
                onBackToSignIn = { navController.navigateUp() }
            )
        }
        composable("profile_setup") {
            ProfileSetupScreen(
                onSetupComplete = {
                    // 메인 화면으로 이동 등
                    navController.navigate("home") {
                        popUpTo(0) // 백스택 전체 클리어
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DateMateTheme {
        MainScreen(onNavigateToSubFirst = fun(_) = Unit, onNavigateToSubSecond = fun() = Unit)
    }
}