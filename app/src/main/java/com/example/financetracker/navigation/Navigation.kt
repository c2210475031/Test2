package com.example.financetracker.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financetracker.presentation.AddTransactionScreen
import com.example.financetracker.presentation.CategoryScreen
import com.example.financetracker.presentation.StartScreen
import com.example.financetracker.presentation.TransactionScreen
import com.example.financetracker.viewmodel.TransactionViewModel
import androidx.compose.ui.Modifier

@SuppressLint("NewApi")
@Composable
fun Navigation(modifier: Modifier) {
    val controller = rememberNavController()
    val viewModel: TransactionViewModel = viewModel()

    NavHost(navController = controller, startDestination = Screen.StartScreen.route) {
        composable(route = Screen.StartScreen.route) {
            StartScreen(
                onContinue = { controller.navigate("transactions") },
                onCategories = { controller.navigate("categories") },
                onAddTransaction = { controller.navigate("addTransaction") })
        }
        composable(route = Screen.TransactionScreen.route) {
            TransactionScreen(viewModel)
        }

        composable(route = Screen.CategoryScreen.route) {
            CategoryScreen() // uses hiltViewModel()
        }

        composable(route = Screen.AddTransactionScreen.route) {
            AddTransactionScreen(onBack = { controller.popBackStack() })
        }
    }
}