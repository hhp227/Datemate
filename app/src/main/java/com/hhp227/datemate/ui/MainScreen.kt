package com.hhp227.datemate.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hhp227.datemate.R

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(checkIfOnline(context)) }
    val navController = rememberNavController()
    val list = listOf(NavigationItem.Home, NavigationItem.Lounge)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    if (isOnline) {
        Scaffold(
            scaffoldState = rememberScaffoldState(),
            topBar = {
                Surface(
                    color = MaterialTheme.colors.primary,
                    elevation = 4.dp
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        navigationIcon = navBackStackEntry?.destination?.route?.takeIf { it !in list.map(NavigationItem::route) }?.let {
                            {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        })
                }
            },
            bottomBar = { BottomNavigationBar(navController, list, navBackStackEntry?.destination) },
            floatingActionButton = {
                if (navBackStackEntry?.destination?.route == NavigationItem.Lounge.route) {
                    FloatingActionButton(onClick = { /*TODO*/ }) {
                        Icon(painter = painterResource(id = R.drawable.ic_add_24), contentDescription = null)
                    }
                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = "Main"
            ) {
                navigation(
                    route = "Main",
                    startDestination = NavigationItem.Home.route
                ) {
                    composable(NavigationItem.Home.route) { HomeScreen() }
                    composable(NavigationItem.Lounge.route) { from ->
                        LoungeScreen(onNavigate = {
                            if (from.lifecycle.currentState == Lifecycle.State.RESUMED) {
                                navController.navigate("PostDetail")
                            }
                            Log.e("TEST", "우왕국")
                        })
                    }
                }
                composable(
                    route = "PostDetail",
                    arguments = emptyList()
                ) { PostDetailScreen() }
                composable(
                    route = "Write",
                    arguments = emptyList()
                ) { WriteScreen() }
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
fun BottomNavigationBar(navController: NavController, list: List<NavigationItem>, currentDestination: NavDestination?) {
    if (currentDestination?.route in list.map(NavigationItem::route)) {
        BottomNavigation {
            list.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title
                        )
                    },
                    alwaysShowLabel = true,
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    }
                )
            }
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