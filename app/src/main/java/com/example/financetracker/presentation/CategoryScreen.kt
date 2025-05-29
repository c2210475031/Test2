package com.example.financetracker.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.model.Category
import com.example.financetracker.viewmodel.CategoryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(viewModel: CategoryViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Categories") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(categories) { category ->
                CategoryCard(category)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = category.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (category.maxNegativeValue < 0)
                    "No limit"
                else
                    "Limit: €${category.maxNegativeValue}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
