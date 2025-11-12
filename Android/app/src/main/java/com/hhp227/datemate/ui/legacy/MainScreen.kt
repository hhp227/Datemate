package com.hhp227.datemate.ui.legacy

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.hhp227.datemate.R
import com.hhp227.datemate.ui.legacy.MainDestinations.MAIN_ROUTE
import com.hhp227.datemate.ui.legacy.MainDestinations.POST_DETAIL_ROUTE
import com.hhp227.datemate.ui.legacy.MainDestinations.POST_KEY
import com.hhp227.datemate.ui.legacy.MainDestinations.USER_DETAIL_ROUTE
import com.hhp227.datemate.ui.legacy.MainDestinations.WRITE_EDIT_ROUTE
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(checkIfOnline(context)) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    if (isOnline) {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                if (currentRoute == "${MainDestinations.POST_DETAIL_ROUTE}/{${MainDestinations.POST_KEY}}") {
                    Text(text = "Sheet content ${currentRoute}")
                } else {
                    Text(text = "Sheet Test")
                }
            }
        ) {
            StandardScaffold(
                navController = navController,
                currentRoute = currentRoute ?: MAIN_ROUTE,
                bottomNavItems = listOf(NavigationItem.Home, NavigationItem.Lounge),
                onActionListener = object : OnActionListener {
                    override fun onShowActionSheet() {
                        coroutineScope.launch {
                            sheetState.show()
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
                                    navController.navigate("${USER_DETAIL_ROUTE}")
                                }
                            })
                        }
                        composable(NavigationItem.Lounge.route) { from ->
                            LoungeScreen(onNavigate = { postKey ->
                                if (from.lifecycle.currentState == Lifecycle.State.RESUMED) {
                                    navController.navigate("${POST_DETAIL_ROUTE}/$postKey")
                                }
                            })
                        }
                    }
                    composable(
                        route = "${USER_DETAIL_ROUTE}",
                        arguments = emptyList()
                    ) {
                        UserDetailScreen()
                    }
                    composable(
                        route = "${POST_DETAIL_ROUTE}/{${POST_KEY}}",
                        arguments = emptyList()
                    ) { PostDetailScreen(sheetState, it.arguments?.getString(POST_KEY) ?: "") }
                    composable(
                        route = WRITE_EDIT_ROUTE,
                        arguments = emptyList()
                    ) {
                        WriteEditScreen()
                    }
                }
            }
        }
    } else {
        OfflineDialog { isOnline = checkIfOnline(context) }
    }
}

@Composable
fun StandardScaffold(
    navController: NavController,
    modifier: Modifier = Modifier,
    currentRoute: String,
    bottomNavItems: List<NavigationItem> = emptyList(),
    onActionListener: OnActionListener,
    content: @Composable (PaddingValues) -> Unit
) {
    val isMainScreen: Boolean = currentRoute in bottomNavItems.map(NavigationItem::route)

    Scaffold(
        modifier = modifier,
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        textAlign = if (!isMainScreen) TextAlign.Start else TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = if (!isMainScreen) {
                    {
                        IconButton(onClick = navController::navigateUp) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                } else null,
                actions = {
                    when (currentRoute) {
                        /*MAIN_ROUTE -> {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Filled.ExitToApp,
                                    contentDescription = null
                                )
                            }
                        }*/
                        "${MainDestinations.POST_DETAIL_ROUTE}/{${MainDestinations.POST_KEY}}" -> {
                            // TODO 여기에 내포스트인지 확인할 조건이 들어갈것
                            IconButton(onClick = onActionListener::onShowActionSheet) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = stringResource(id = R.string.more)
                                )
                            }
                        }
                        MainDestinations.WRITE_EDIT_ROUTE -> {
                            IconButton(onClick = { /*TODO*/ }) {
                                Text(text = "전송")
                            }
                        }
                    }
                })
        },
        bottomBar = {
            if (isMainScreen) {
                BottomNavigation {
                    bottomNavItems.forEach { item ->
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = item.title
                                )
                            },
                            //label = { Text(text = item.title) },
                            alwaysShowLabel = true,
                            selected = currentRoute == item.route,
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
        },
        floatingActionButton = {
            if (currentRoute == NavigationItem.Lounge.route) {
                FloatingActionButton(onClick = {
                    navController.navigate(MainDestinations.WRITE_EDIT_ROUTE)
                }) {
                    Icon(painter = painterResource(id = R.drawable.ic_add_24), contentDescription = null)
                }
            }
        }
    ) { innerPaddingValues ->
        content(innerPaddingValues)
    }
}

interface OnActionListener {
    fun onShowActionSheet()
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