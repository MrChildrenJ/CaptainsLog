package com.example.captainslog.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.captainslog.navigation.NavGraph
import com.example.captainslog.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine if bottom bar should be shown
    val showBottomBar = when (currentRoute) {
        Screen.LogList.route -> true
        Screen.Search.route -> true
        Screen.Friends.route -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Logs") },
                        label = { Text("Logs") },
                        selected = currentRoute == Screen.LogList.route,
                        onClick = {
                            navController.navigate(Screen.LogList.route) {
                                popUpTo(Screen.LogList.route) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search") },
                        selected = currentRoute == Screen.Search.route,
                        onClick = {
                            navController.navigate(Screen.Search.route)
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Friends") },
                        label = { Text("Friends") },
                        selected = currentRoute == Screen.Friends.route,
                        onClick = {
                            navController.navigate(Screen.Friends.route)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
