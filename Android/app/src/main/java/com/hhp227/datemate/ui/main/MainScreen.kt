package com.hhp227.datemate.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hhp227.datemate.ui.main.chatroom.ChatRoomScreen
import com.hhp227.datemate.ui.main.discover.DiscoverScreen
import com.hhp227.datemate.ui.main.favorite.FavoriteScreen
import com.hhp227.datemate.ui.main.lounge.LoungeScreen

@Composable
fun MainScreen(
    onNavigateToSubFirst: (String) -> Unit,
    onNavigateToSubSecond: () -> Unit,
    onNavigateToMyProfile: () -> Unit
) {
    val viewModel: MainViewModel = viewModel()
    val bottomNavController = rememberNavController()
    val items = listOf(
        Triple("discover", Icons.Default.Home, "탐색"),
        Triple("lounge", Icons.AutoMirrored.Filled.List, "라운지"),
        Triple("favorite", Icons.Default.Favorite, "관심"),
        Triple("chatroom", Icons.Default.ChatBubble, "채팅")
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
                    IconButton(onClick = onNavigateToMyProfile) {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation {
                val currentRoute =
                    bottomNavController.currentBackStackEntryAsState().value?.destination?.route

                items.forEach { (route, icon, label) ->
                    BottomNavigationItem(
                        selected = currentRoute == route,
                        onClick = {
                            bottomNavController.navigate(route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "discover",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("discover") { DiscoverScreen(onNavigateToSubFirst = onNavigateToSubFirst) }
            composable("lounge") { LoungeScreen(onNavigateToSubSecond = onNavigateToSubSecond) }
            composable("favorite") { FavoriteScreen() }
            composable("chatroom") { ChatRoomScreen() }
        }
    }
}