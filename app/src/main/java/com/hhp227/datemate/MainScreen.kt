package com.hhp227.datemate

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hhp227.datemate.ui.NavigationItem

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(checkIfOnline(context)) }
    val navController = rememberNavController()

    if (isOnline) {
        Scaffold(
            scaffoldState = rememberScaffoldState(),
            topBar = {
                Surface(
                    color = MaterialTheme.colors.primary,
                    elevation = 4.dp
                ) {
                    TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) })
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) {
            NavHost(
                navController = navController,
                startDestination = NavigationItem.Home.route
            ) {
                composable(NavigationItem.Home.route) { HomeScreen() }
                composable(NavigationItem.Lounge.route) { LoungeScreen() }
            }
        }
    } else {
        OfflineDialog { isOnline = checkIfOnline(context) }
    }
}

@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = fun() = Unit,
        title = { Text(text = stringResource(R.string.connection_error_title)) },
        text = { Text(text = stringResource(R.string.connection_error_message)) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry_label))
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation {
        listOf(NavigationItem.Home, NavigationItem.Lounge).forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title
                    )
                },
                alwaysShowLabel = true,
                selected = false,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true

                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                    }
                }
            )
        }
    }
}

@Suppress("DEPRECATION")
private fun checkIfOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false

        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } else {
        cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}