package com.example.financetracker.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financetracker.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(modifier: Modifier, navController: NavController) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Welcome") },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(Screen.ProfileScreen.route) {
                                popUpTo(Screen.ProfileScreen.route) { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }


        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to Finance Tracker", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { navController.navigate(Screen.TransactionScreen.route) }) {
                Text("View Transactions")
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { navController.navigate(Screen.CategoryScreen.route) }) {
                Text("View Categories")
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { navController.navigate(Screen.AddTransactionScreen.route) }) {
                Text("Add Transaction")
            }
        }
    }
}