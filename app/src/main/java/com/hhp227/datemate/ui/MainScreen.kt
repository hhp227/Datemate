package com.hhp227.datemate.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
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
import com.hhp227.datemate.ui.MainDestinations.MAIN_ROUTE
import com.hhp227.datemate.ui.MainDestinations.POST_DETAIL_ROUTE
import com.hhp227.datemate.ui.MainDestinations.POST_KEY
import com.hhp227.datemate.ui.MainDestinations.USER_DETAIL_ROUTE
import com.hhp227.datemate.ui.MainDestinations.WRITE_EDIT_ROUTE
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(checkIfOnline(context)) }
    val navController = rememberNavController()
    val list = listOf(NavigationItem.Home, NavigationItem.Lounge)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    if (isOnline) {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                if (navBackStackEntry?.destination?.route == "$POST_DETAIL_ROUTE/{$POST_KEY}") {
                    Text(text = "Sheet content ${navBackStackEntry?.destination?.route}")
                } else {
                    Text(text = "Sheet Test")
                }
            }
        ) {
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
                                    textAlign = navBackStackEntry?.destination?.route?.takeIf { it !in list.map(NavigationItem::route) }?.let { TextAlign.Start } ?: TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            navigationIcon = navBackStackEntry?.destination?.route?.takeIf { it !in list.map(NavigationItem::route) }?.let {
                                {
                                    IconButton(onClick = navController::navigateUp) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            },
                            actions = {
                                when (navBackStackEntry?.destination?.route) {
                                    MAIN_ROUTE -> {
                                        IconButton(onClick = { /*TODO*/ }) {
                                            Icon(
                                                imageVector = Icons.Filled.ExitToApp,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                    "$POST_DETAIL_ROUTE/{$POST_KEY}" -> {
                                        // TODO 여기에 내포스트인지 확인할 조건이 들어갈것
                                        IconButton(onClick = {
                                            coroutineScope.launch {
                                                sheetState.show()
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.MoreVert,
                                                contentDescription = stringResource(id = R.string.more)
                                            )
                                        }
                                    }
                                    WRITE_EDIT_ROUTE -> {
                                        IconButton(onClick = { /*TODO*/ }) {
                                            Text(text = "전송")
                                        }
                                    }
                                }
                            })
                    }
                },
                bottomBar = { BottomNavigationBar(navController, list, navBackStackEntry?.destination) },
                floatingActionButton = {
                    if (navBackStackEntry?.destination?.route == NavigationItem.Lounge.route) {
                        FloatingActionButton(onClick = {
                            if (navBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                navController.navigate(WRITE_EDIT_ROUTE)
                            }
                        }) {
                            Icon(painter = painterResource(id = R.drawable.ic_add_24), contentDescription = null)
                        }
                    }
                }
            ) { innerPaddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = MAIN_ROUTE,
                    modifier = Modifier.padding(innerPaddingValues)
                ) {
                    navigation(
                        route = MAIN_ROUTE,
                        startDestination = NavigationItem.Home.route
                    ) {
                        composable(NavigationItem.Home.route) { from ->
                            HomeScreen(onNavigate = {
                                if (from.lifecycle.currentState == Lifecycle.State.RESUMED) {
                                    navController.navigate("$USER_DETAIL_ROUTE")
                                }
                            })
                        }
                        composable(NavigationItem.Lounge.route) { from ->
                            LoungeScreen(onNavigate = { postKey ->
                                if (from.lifecycle.currentState == Lifecycle.State.RESUMED) {
                                    navController.navigate("$POST_DETAIL_ROUTE/$postKey")
                                }
                            })
                        }
                    }
                    composable(
                        route = "$USER_DETAIL_ROUTE",
                        arguments = emptyList()
                    ) {
                        UserDetailScreen()
                    }
                    composable(
                        route = "$POST_DETAIL_ROUTE/{$POST_KEY}",
                        arguments = emptyList()
                    ) { PostDetailScreen(sheetState, it.arguments?.getString(POST_KEY) ?: "") }
                    composable(
                        route = WRITE_EDIT_ROUTE,
                        arguments = emptyList()
                    ) { WriteEditScreen() }
                }
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
                    //label = { Text(text = item.title) },
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

object MainDestinations {
    const val MAIN_ROUTE = "Main"
    const val WRITE_EDIT_ROUTE = "WriteEdit"
    const val POST_DETAIL_ROUTE = "PostDetail"
    const val POST_KEY = "PostKey"
    const val USER_DETAIL_ROUTE = "UserDetail"
}