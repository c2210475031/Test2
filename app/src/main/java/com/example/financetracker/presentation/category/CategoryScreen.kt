package com.example.financetracker.presentation.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financetracker.MainActivity
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.CategoryType
import com.example.financetracker.database.model.User
import com.example.financetracker.navigation.Screen
import com.example.financetracker.presentation.CreateUserDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    modifier: Modifier,
    navController: NavController,
) {
    val viewModel = MainActivity.globalViewModel
    val categories by viewModel.userCategories.observeAsState(emptyList())

    var showCreateDialog by remember { mutableStateOf(false) }
    val userId by viewModel.activeUserId.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Categories") }, actions = {
                    IconButton(onClick = {
                        showCreateDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Home",
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
            items(categories) { category ->
                CategoryCard(
                    category,
                    onDelete = { viewModel.deleteCategory(it) },
                    onEdit = { category ->
                        selectedCategoryId = category.id
                        showEditDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (showCreateDialog && userId != null) {
            CreateOrEditCategoryDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, type, limit ->
                    val category = Category(
                        name = name,
                        type = type,
                        maxNegativeValue = limit,
                        userId = userId!!
                    )
                    viewModel.insertCategory(category)
                    showCreateDialog = false
                }
            )
        }

        if (showEditDialog && selectedCategoryId != null) {
            EditCategoryDialog(
                categoryId = selectedCategoryId!!,
                onDismiss = { showEditDialog = false },
                onSave = { name, limit ->
                    val updated = categories.find { it.id == selectedCategoryId }?.copy(
                        name = name,
                        maxNegativeValue = limit
                    )
                    if (updated != null) {
                        viewModel.updateCategory(updated)
                    }
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onDelete: (Category) -> Unit,
    onEdit: (Category) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (category.maxNegativeValue < 0) "No limit"
                    else "Limit: €${category.maxNegativeValue}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(text = "${category.id}", style = MaterialTheme.typography.bodySmall)
            }

            // Delete Button
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { onEdit(category) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit category",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { onDelete(category) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete category",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun CreateOrEditCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, CategoryType, Double) -> Unit,
    initialName: String = "",
    initialType: CategoryType = CategoryType.EXPENSE,
    initialLimit: Double = -1.0
) {
    var name by remember { mutableStateOf(initialName) }
    var type by remember { mutableStateOf(initialType) }
    var limitInput by remember { mutableStateOf(if (initialLimit >= 0) initialLimit.toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val limit = limitInput.toDoubleOrNull() ?: -1.0
                onConfirm(name, type, limit)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Create Category") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Type:", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == CategoryType.INCOME,
                        onClick = { type = CategoryType.INCOME },
                        label = { Text("Income") }
                    )
                    FilterChip(
                        selected = type == CategoryType.EXPENSE,
                        onClick = { type = CategoryType.EXPENSE },
                        label = { Text("Expense") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = limitInput,
                    onValueChange = { limitInput = it },
                    label = { Text("Max Negative Value (-1 for none)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
fun EditCategoryDialog(
    categoryId: Int,
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit
) {
    val viewModel = MainActivity.globalViewModel
    val categories by viewModel.userCategories.observeAsState(emptyList())
    val category = categories.find { it.id == categoryId }

    if (category == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss) { Text("OK") }
            },
            title = { Text("Error") },
            text = { Text("Category not found.") }
        )
        return
    }

    var name by remember { mutableStateOf(category.name) }
    var limit by remember { mutableStateOf(category.maxNegativeValue.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val updatedLimit = limit.toDoubleOrNull() ?: -1.0
                onSave(name, updatedLimit)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Edit Category") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = { Text("Max Negative Value (-1 for unlimited)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}