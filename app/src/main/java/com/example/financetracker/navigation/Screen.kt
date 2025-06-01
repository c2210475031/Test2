package com.example.financetracker.navigation

sealed class Screen(val route: String) {
    object StartScreen: Screen(route = "start")
    object TransactionScreen: Screen(route = "transactions")
    object CategoryScreen: Screen(route = "categories")
    object AddTransactionScreen: Screen(route = "addTransaction")
    object ProfileScreen: Screen(route = "profile")
    object AddCategoryScreen: Screen(route = "addCategories")
    object EditTransactionScreen : Screen("editTransaction/{transactionId}"){
        fun createRoute(transactionId: Int) = "editTransaction/$transactionId"
    }
    object EditCategoryScreen : Screen("editCategory/{categoryId}"){
        fun createRoute(categoryId: Int) = "editCategory/$categoryId"
    }
}