package com.example.financetracker.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.Transaction
import com.example.financetracker.navigation.Screen
import com.example.financetracker.viewmodel.GlobalViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier,
    navController: NavController,
) {
    val viewModel = MainActivity.globalViewModel
    val transactions by viewModel.userTransactions.observeAsState(initial = emptyList())
    val categories by viewModel.userCategories.observeAsState(emptyList())
    val selectedFilter by viewModel.filter.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Transactions") },
                    actions = {
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
                    },
                    navigationIcon = {
                        IconButton(onClick = {navController.popBackStack()}) {Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")}
                    })
            }
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Type Filter
            Box(modifier = Modifier.padding(16.dp)) {
                Button(onClick = { expanded = true }) {
                    Text("Type: $selectedFilter")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("All", "Income", "Expenses").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.setFilter(option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Date Range Filter
            DateFilterSection(viewModel)



            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionCard(transaction, categories, onDelete = { viewModel.deleteTransaction(it) }, navController = navController)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// Helper to parse input
@RequiresApi(Build.VERSION_CODES.O)
fun String.toLocalDateOrNull(): LocalDate? =
    try {
        LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
    } catch (e: DateTimeParseException) {
        null
    }

@Composable
fun TransactionCard(transaction: Transaction, categories: List<Category>, onDelete: (Transaction)-> Unit, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${if (transaction.isPositive) "+" else "-"} €${transaction.amount}",
                color = if (transaction.isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Text(text = "${transaction.timestamp}", style = MaterialTheme.typography.bodySmall)
            Text(text = "${transaction.categoryId}", style = MaterialTheme.typography.bodySmall)
            var x = "yyyy-MM-dd";
            for (temp:Category in categories){
                if(temp.id == transaction.categoryId) x = "${temp.name}";
            }
            Text(text = x, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            // Delete Button
            Button(
                onClick = { onDelete(transaction) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete", color = MaterialTheme.colorScheme.onError)
            }

            Button(
                onClick = {
                    //navController.navigate("editTransaction/${transaction.ID}")
                    navController.navigate(Screen.EditTransactionScreen.createRoute(transaction.id))
                }
            ) {
                Text("Edit")
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateFilterSection(viewModel: GlobalViewModel) {
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { showStartPicker = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Start: ${startDate?.format(formatter) ?: "Select"}")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { showEndPicker = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("End: ${endDate?.format(formatter) ?: "Select"}")
            }
        }
    }

    if (showStartPicker) {
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            onDateChange = {
                viewModel.setStartDate(it)
                showStartPicker = false
            }
        )
    }

    if (showEndPicker) {
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            onDateChange = {
                viewModel.setEndDate(it)
                showEndPicker = false
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateChange: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis
                if (millis != null) {
                    val selectedDate = Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    onDateChange(selectedDate)
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        title = { Text("Pick a Date") },
        text = {
            DatePicker(state = datePickerState)
        }
    )
}