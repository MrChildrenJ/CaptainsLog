package com.example.captainslog.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.captainslog.ui.screens.*
import com.example.captainslog.viewmodel.LogViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    logViewModel: LogViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.LogList.route,
        modifier = modifier
    ) {
        // Log List Screen
        composable(Screen.LogList.route) {
            LogListScreen(
                onLogClick = { logId ->
                    navController.navigate(Screen.LogDetail.createRoute(logId))
                },
                onCreateNew = {
                    navController.navigate(Screen.Record.route)
                },
                viewModel = logViewModel
            )
        }

        // Record Screen
        composable(Screen.Record.route) {
            RecordLogScreen(
                onSave = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        // Log Detail Screen
        composable(
            route = Screen.LogDetail.route,
            arguments = listOf(navArgument("logId") { type = NavType.StringType })
        ) { backStackEntry ->
            val logId = backStackEntry.arguments?.getString("logId")
            val logEntry = logViewModel.getLogById(logId ?: "")

            if (logEntry != null) {
                LogDetailScreen(
                    logEntry = logEntry,
                    onBack = { navController.popBackStack() },
                    onShare = {
                        // TODO: Implement share dialog
                    },
                    onDelete = {
                        logViewModel.deleteLog(logEntry.id)
                        navController.popBackStack()
                    }
                )
            }
        }

        // Search Screen
        composable(Screen.Search.route) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onResultClick = { logId ->
                    navController.navigate(Screen.LogDetail.createRoute(logId))
                }
            )
        }

        // Friends Screen
        composable(Screen.Friends.route) {
            FriendsListScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
