package com.hhp227.datemate.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.insets.ProvideWindowInsets
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.data.repository.UserRepository.SignInState
import com.hhp227.datemate.ui.auth.forgotpassword.ForgotPasswordScreen
import com.hhp227.datemate.ui.auth.phoneauth.PhoneAuthScreen
import com.hhp227.datemate.ui.auth.profilesetup.GenderSetupScreen
import com.hhp227.datemate.ui.auth.profilesetup.InfoSetupScreen
import com.hhp227.datemate.ui.auth.profilesetup.PhotoSetupScreen
import com.hhp227.datemate.ui.auth.profilesetup.ProfileSetupViewModel
import com.hhp227.datemate.ui.detail.SubFirstScreen
import com.hhp227.datemate.ui.main.MainScreen
import com.hhp227.datemate.ui.postdetail.PostDetailScreen
import com.hhp227.datemate.ui.auth.signin.SignInScreen
import com.hhp227.datemate.ui.auth.signup.SignUpScreen
import com.hhp227.datemate.ui.myprofile.MyProfileScreen
import com.hhp227.datemate.ui.splash.SplashScreen
import com.hhp227.datemate.ui.theme.DateMateTheme

@Composable
fun DateMateApp() {
    ProvideWindowInsets {
        DateMateTheme {
            val signInState by InjectorUtils.getUserRepository(LocalContext.current.applicationContext)
                .signInStateFlow
                .collectAsState(SignInState.Loading)

            when (signInState) {
                SignInState.SignIn -> AppNavHost()
                SignInState.SignOut -> SignInNavHost()
                SignInState.Loading -> SplashScreen()
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
                onNavigateToSubSecond = { navController.navigate("sub_second") },
                onNavigateToMyProfile = { navController.navigate("my_profile") }
            )
        }
        composable(
            route = "sub_first/{data}",
            arguments = listOf(
                navArgument("data") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            SubFirstScreen(
                viewModel = viewModel(factory = InjectorUtils.provideDetailViewModelFactory(backStackEntry, LocalContext.current.applicationContext)),
                onNavigateUp = navController::navigateUp
            )
        }
        composable("sub_second") { PostDetailScreen(onNavigateUp = navController::navigateUp) }
        composable("my_profile") { MyProfileScreen(onNavigateUp = navController::navigateUp) }
    }
}

@Composable
fun SignInNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "sign_in"
    ) {
        composable("sign_in") {
            SignInScreen(
                onSignUp = { navController.navigate("sign_up") },
                onForgotPassword = { navController.navigate("forgot_password") },
                onProfileSetup = {
                    navController.navigate("profile_setup") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("sign_up") {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("phone_auth") {
                        popUpTo("sign_in") { inclusive = true }
                    }
                },
                onBackToSignIn = navController::navigateUp
            )
        }
        composable("phone_auth") {
            PhoneAuthScreen(
                onVerified = {
                    navController.navigate("profile_setup") {
                        popUpTo("phone_auth") { inclusive = true }
                    }
                }
            )
        }
        navigation(
            startDestination = "gender_setup",
            route = "profile_setup"
        ) {
            composable("gender_setup") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("profile_setup")
                }
                val viewModel: ProfileSetupViewModel =
                    viewModel(parentEntry, factory = InjectorUtils.provideProfileSetupViewModelFactory(LocalContext.current))

                GenderSetupScreen(
                    viewModel = viewModel,
                    onNext = { navController.navigate("photo_setup") }
                )
            }
            composable("photo_setup") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("profile_setup")
                }
                val viewModel: ProfileSetupViewModel =
                    viewModel(parentEntry, factory = InjectorUtils.provideProfileSetupViewModelFactory(LocalContext.current))

                PhotoSetupScreen(
                    viewModel = viewModel,
                    onNext = { navController.navigate("info_setup") },
                    onNavigateUp = navController::navigateUp
                )
            }
            composable("info_setup") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("profile_setup")
                }
                val viewModel: ProfileSetupViewModel =
                    viewModel(parentEntry, factory = InjectorUtils.provideProfileSetupViewModelFactory(LocalContext.current))

                InfoSetupScreen(
                    viewModel = viewModel,
                    onSetupComplete = {
                        navController.navigate("home") { popUpTo(0) }
                    },
                    onNavigateUp = navController::navigateUp
                )
            }
        }
        composable("forgot_password") {
            ForgotPasswordScreen(onBackToSignIn = navController::navigateUp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DateMateTheme {
        MainScreen(
            onNavigateToSubFirst = fun(_) = Unit,
            onNavigateToSubSecond = fun() = Unit,
            onNavigateToMyProfile = fun() = Unit
        )
    }
}