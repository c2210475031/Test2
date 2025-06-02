package com.example.financetracker.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financetracker.MainActivity
import com.example.financetracker.database.AppDatabase
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.repository.TransactionRepository
import com.example.financetracker.navigation.Screen
import com.example.financetracker.viewmodel.GlobalViewModel
import com.example.financetracker.viewmodel.GlobalViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    modifier: Modifier,
    navController: NavController,
) {
    val viewModel = MainActivity.globalViewModel
    val categories by viewModel.userCategories.observeAsState(emptyList())

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Categories") }, actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.StartScreen.route) {
                            popUpTo(Screen.StartScreen.route) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Exit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }, navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                })
            }
        }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(categories) {category ->
                CategoryCard(category, onDelete = { viewModel.deleteCategory(it)}, navController = navController)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category, onDelete: (Category) -> Unit, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row {
            Column(modifier = Modifier.padding(16.dp)) {
               Text(text = category.name, style = MaterialTheme.typography.titleMedium)
              Text(
                  text = if (category.maxNegativeValue < 0) "No limit"
                  else "Limit: €${category.maxNegativeValue}",
                   style = MaterialTheme.typography.bodySmall
               )
               Text(text = "${category.id}", style = MaterialTheme.typography.bodySmall)
            }

            // Delete Button
               Button(
                    onClick = { onDelete(category) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                 Text("Delete", color = MaterialTheme.colorScheme.onError)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        navController.navigate(Screen.EditCategoryScreen.createRoute(category.id))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }


        }
    }
}
