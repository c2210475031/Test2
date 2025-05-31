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
import com.example.financetracker.database.AppDatabase
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.CategoryType
import com.example.financetracker.database.repository.TransactionRepository
import com.example.financetracker.navigation.Screen
import com.example.financetracker.viewmodel.GlobalViewModel
import com.example.financetracker.viewmodel.GlobalViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(modifier: Modifier, navController: NavController, transactionId: Int) {
    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getDatabase(context)
    val repository = TransactionRepository(db.transactionDao(), db.categoryDao())
    val viewModel: GlobalViewModel = viewModel(factory = GlobalViewModelFactory(repository))

    val transactions by viewModel.allTransactions.observeAsState(emptyList())
    val categories by viewModel.allCategories.observeAsState(emptyList())
    val transaction = transactions.find { it.ID == transactionId } ?: return

    var valueInput by remember { mutableStateOf(transaction.amount.toString()) }
    var dateInput by remember {
        mutableStateOf(
            transaction.timestamp.atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(
                DateTimeFormatter.ISO_DATE)
        )
    }
    var isPositive by remember { mutableStateOf(transaction.isPositive) }
    var selectedCategoryId by remember { mutableStateOf(transaction.categoryId) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Transaction") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = valueInput,
                onValueChange = { valueInput = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            DatePickerField(
                label = "Date",
                selectedDate = dateInput,
                onDateSelected = { dateInput = it }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Category dropdown
            OutlinedTextField(
                value = categories.find { it.id == selectedCategoryId }?.name ?: "",
                onValueChange = {},
                label = { Text("Category") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select category")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategoryId = category.id
                            expanded = false
                        }
                    )
                }
            }

            Row {
                Text("Type:")
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = isPositive,
                    onClick = { isPositive = true },
                    label = { Text("Income") })
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = !isPositive,
                    onClick = { isPositive = false },
                    label = { Text("Expense") })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val updatedTransaction = transaction.copy(
                        amount = valueInput.toDoubleOrNull() ?: return@Button,
                        timestamp = LocalDate.parse(dateInput).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant(),
                        categoryId = selectedCategoryId ?: return@Button,
                        isPositive = isPositive
                    )
                    viewModel.updateTransaction(updatedTransaction)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}
