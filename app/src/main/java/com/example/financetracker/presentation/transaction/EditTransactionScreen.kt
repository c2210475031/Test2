package com.example.financetracker.presentation.transaction


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financetracker.MainActivity
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(modifier: Modifier, navController: NavController, transactionId: Int) {
    val viewModel = MainActivity.globalViewModel

    val transactions by viewModel.userTransactions.observeAsState(emptyList())
    val categories by viewModel.userCategories.observeAsState(emptyList())
    val transaction = transactions.find { it.id == transactionId } ?: return

    var valueInput by remember { mutableStateOf(transaction.amount.toString()) }
    var dateInput by remember {
        mutableStateOf(
            transaction.timestamp.atZone(ZoneId.systemDefault()).toLocalDate().format(
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
                        timestamp = LocalDate.parse(dateInput).atStartOfDay(ZoneId.systemDefault()).toInstant(),
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
