package com.example.test2

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.test2.ui.AddTransactionScreen
import com.example.test2.ui.CategoryScreen
import com.example.test2.ui.StartScreen
import com.example.test2.ui.TransactionScreen
import com.example.test2.viewmodel.TransactionViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: TransactionViewModel = viewModel()

    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            StartScreen(onContinue = { navController.navigate("transactions") },
                onCategories = { navController.navigate("categories") },
                onAddTransaction = { navController.navigate("addTransaction")})
        }
        composable("transactions") {
            TransactionScreen(viewModel)
        }

        composable("categories") {
            CategoryScreen() // uses hiltViewModel()
        }

        composable("addTransaction") {
            AddTransactionScreen(onBack = { navController.popBackStack() })
        }
    }
}