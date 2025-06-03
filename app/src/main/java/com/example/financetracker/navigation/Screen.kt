package com.example.financetracker.navigation

sealed class Screen(val route: String) {
    object StartScreen: Screen(route = "start")
    object TransactionScreen: Screen(route = "transactions")
    object CategoryScreen: Screen(route = "categories")
    object ProfileScreen: Screen(route = "profile")
    object TransactionChartScreen: Screen(route = "Graphics")
}