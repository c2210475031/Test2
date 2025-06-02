package com.example.financetracker.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financetracker.MainActivity
import com.example.financetracker.database.AppDatabase
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.CategoryType
import com.example.financetracker.database.model.User
import com.example.financetracker.database.repository.TransactionRepository
import com.example.financetracker.navigation.Screen
import com.example.financetracker.viewmodel.GlobalViewModel
import com.example.financetracker.viewmodel.GlobalViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(modifier: Modifier, navController: NavController) {
    var nameInput by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(CategoryType.EXPENSE) }
    var maxLimitInput by remember { mutableStateOf("") }

    val viewModel = MainActivity.globalViewModel
    val userId by viewModel.activeUserId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Category") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.StartScreen.route) {
                            popUpTo(Screen.StartScreen.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Home, contentDescription = "Exit")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Type:")
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = selectedType == CategoryType.INCOME,
                    onClick = { selectedType = CategoryType.INCOME },
                    label = { Text("Income") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = selectedType == CategoryType.EXPENSE,
                    onClick = { selectedType = CategoryType.EXPENSE },
                    label = { Text("Expense") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = maxLimitInput,
                onValueChange = { maxLimitInput = it },
                label = { Text("Max Negative Value (-1 for none)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = userId != null && nameInput.isNotBlank(),
                onClick = {
                    val maxLimit = maxLimitInput.toDoubleOrNull() ?: -1.0

                    if (nameInput.isNotBlank() && userId != null) {
                        val category = Category(
                            name = nameInput,
                            type = selectedType,
                            maxNegativeValue = maxLimit,
                            userId = userId!!
                        )
                        viewModel.insertCategory(category)
                        navController.navigate(Screen.CategoryScreen.route)
                    }
                }
            ) {
                Text("Save")
            }
        }
    }
}
