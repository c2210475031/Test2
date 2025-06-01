package com.example.financetracker.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.presentation.AddCategoryScreen
import com.example.financetracker.presentation.AddTransactionScreen
import com.example.financetracker.presentation.CategoryScreen
import com.example.financetracker.presentation.EditTransactionScreen
import com.example.financetracker.presentation.ProfileScreen
import com.example.financetracker.presentation.StartScreen
import com.example.financetracker.presentation.TransactionScreen
import com.example.financetracker.presentation.EditCategoryScreen


@SuppressLint("NewApi")
@Composable
fun Navigation(modifier: Modifier) {
    val controller = rememberNavController()

    NavHost(navController = controller, startDestination = Screen.StartScreen.route) {
        composable(route = Screen.StartScreen.route) {
            StartScreen(modifier = modifier, navController = controller)
        }
        composable(route = Screen.TransactionScreen.route) {
            TransactionScreen(modifier = modifier, navController = controller)
        }

        composable(route = Screen.CategoryScreen.route) {
            CategoryScreen(modifier = modifier, navController = controller) // uses hiltViewModel()
        }

        composable(route = Screen.AddTransactionScreen.route) {
            AddTransactionScreen(modifier = modifier, navController = controller)
        }

        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen(modifier = modifier, navController = controller)
        }

        composable(route = Screen.AddCategoryScreen.route) {
            AddCategoryScreen(modifier = modifier, navController = controller)
        }

        composable("editTransaction/{transactionId}") { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")?.toIntOrNull()
            if (transactionId != null) {
                EditTransactionScreen(modifier = modifier, transactionId = transactionId, navController = controller)
            }
        }

        composable("editCategory/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
            if (categoryId != null) {
                EditCategoryScreen(categoryId = categoryId, navController = controller)
            }
        }

    }
}