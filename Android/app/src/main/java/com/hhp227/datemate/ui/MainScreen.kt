package com.hhp227.datemate.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@Composable
fun MainScreen(onNavigateToSubFirst: (String) -> Unit, onNavigateToSubSecond: () -> Unit) {
    val viewModel: MainViewModel = viewModel()
    val bottomNavController = rememberNavController()
    val items = listOf(
        BottomNavItem("first", Icons.Default.Home, "First"),
        BottomNavItem("second", Icons.Default.Favorite, "Second")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Main Screen",
                        textAlign = TextAlign.Center,
                    )
                },
                actions = {
                    IconButton(onClick = {
                        // 메뉴 항목 예시
                        //SnackbarHostState().showSnackbar("Settings clicked")
                    }) {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation {
                val currentRoute =
                    bottomNavController.currentBackStackEntryAsState().value?.destination?.route

                items.forEach { item ->
                    BottomNavigationItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "first",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("first") { LoungeScreen(onNavigateToSubFirst) }
            composable("second") { SecondScreen(onNavigateToSubSecond) }
        }
    }
}

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)