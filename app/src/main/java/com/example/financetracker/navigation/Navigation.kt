package com.example.financetracker.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.presentation.AddTransactionScreen
import com.example.financetracker.presentation.AddCategoryScreen
import com.example.financetracker.presentation.CategoryScreen
import com.example.financetracker.presentation.StartScreen
import com.example.financetracker.presentation.TransactionScreen

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
            CategoryScreen(modifier = modifier, navController = controller)
        }

        composable(route = Screen.AddTransactionScreen.route) {
            AddTransactionScreen(modifier = modifier, navController = controller)
        }

        composable(route = Screen.AddCategoryScreen.route) {
            AddCategoryScreen(modifier = modifier, navController = controller)
        }
    }
}