package com.example.financetracker.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.financetracker.database.repository.TransactionRepository
import com.example.financetracker.navigation.Screen
import com.example.financetracker.viewmodel.GlobalViewModel
import com.example.financetracker.viewmodel.GlobalViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryScreen(categoryId: Int, navController: NavController) {
    val viewModel = MainActivity.globalViewModel

    val categories by viewModel.userCategories.observeAsState(emptyList())
    val category = categories.find { it.id == categoryId }

    if (category == null) {
        Text("Category not found")
        return
    }

    var name by remember { mutableStateOf(category.name) }
    var limit by remember { mutableStateOf(category.maxNegativeValue.toString()) }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Category") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(Modifier.padding(16.dp).padding(padding)) {

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = limit,
                onValueChange = { limit = it },
                label = { Text("Max Negative Value (-1 for unlimited)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val updated = category.copy(
                    name = name,
                    maxNegativeValue = limit.toDoubleOrNull() ?: -1.0
                )
                viewModel.updateCategory(updated)
                navController.popBackStack()
            }) {
                Text("Save")
            }
        }
    }
}