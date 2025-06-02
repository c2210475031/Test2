package com.example.financetracker.presentation.transaction

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financetracker.MainActivity
import com.example.financetracker.database.model.Transaction
import com.example.financetracker.navigation.Screen
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(modifier: Modifier, navController: NavController) {
    var valueInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_DATE)) }
    var categoryInput by remember { mutableStateOf("") }
    var isPositive by remember { mutableStateOf(true) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val viewModel = MainActivity.globalViewModel
    val userId by viewModel.activeUserId.collectAsState()
    val categories by viewModel.userCategories.observeAsState(emptyList())

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Add Transaction") }, actions = {
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = valueInput,
                onValueChange = { valueInput = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            DatePickerField(
                label = "Date", selectedDate = dateInput, onDateSelected = { dateInput = it })
            Spacer(modifier = Modifier.height(8.dp))



            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = categories.find { it.id == selectedCategoryId }
                        ?.let { "${it.name} (ID: ${it.id})" } ?: "",
                    onValueChange = {},
                    label = { Text("Select Category") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select category")
                        }
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text("${category.name} (ID: ${category.id})") },
                            onClick = {
                                selectedCategoryId = category.id
                                expanded = false
                            }
                        )
                    }
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
                modifier = Modifier.fillMaxWidth(),
                enabled = userId != null && valueInput.isNotBlank(),
                onClick = {
                    if (valueInput.isNotBlank() && userId != null) {
                        val transaction = Transaction(
                            amount = valueInput.toDoubleOrNull() ?: 0.0,
                            timestamp = Instant.now(),
                            categoryId = selectedCategoryId
                                ?: return@Button, // Skip if not selected
                            isPositive = isPositive,
                            userId = userId!!
                        )
                        viewModel.insertTransaction(transaction)
                        navController.navigate(Screen.TransactionScreen.route)
                    }
                }
            ) {
                Text("Save")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerField(
    label: String, selectedDate: String, onDateSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick date")
            }
        })

    if (showDialog) {
        DatePickerDialog(
            initialDate = selectedDate,
            onDismiss = { showDialog = false },
            onDateSelected = {
                onDateSelected(it)
                showDialog = false
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDialog(
    initialDate: String, onDismiss: () -> Unit, onDateSelected: (String) -> Unit
) {
    val current = try {
        LocalDate.parse(initialDate)
    } catch (e: Exception) {
        LocalDate.now()
    }

    val state =
        rememberDatePickerState(initialSelectedDateMillis = current.toEpochDay() * 24 * 60 * 60 * 1000)

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(
            onClick = {
                val selectedMillis = state.selectedDateMillis ?: return@TextButton
                val selectedDate = LocalDate.ofEpochDay(selectedMillis / (24 * 60 * 60 * 1000))
                onDateSelected(selectedDate.toString())
            }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = state)
    }
}