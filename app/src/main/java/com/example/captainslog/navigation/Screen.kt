package com.example.captainslog.navigation

sealed class Screen(val route: String) {
    object LogList : Screen("log_list")
    object Record : Screen("record")
    object LogDetail : Screen("log_detail/{logId}") {
        fun createRoute(logId: String) = "log_detail/$logId"
    }
    object Friends : Screen("friends")
    object Search : Screen("search")
    object SharedLogs : Screen("shared_logs")
}
