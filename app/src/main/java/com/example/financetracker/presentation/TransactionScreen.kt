package com.example.financetracker.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financetracker.model.TransactionF
import com.example.financetracker.viewmodel.TransactionViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.financetracker.navigation.Screen
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
    viewModel: TransactionViewModel
) {
    val transactions by viewModel.transactions.collectAsState()
    val selectedFilter by viewModel.filter.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    //   var startInput by remember { mutableStateOf("") }
    //   var endInput by remember { mutableStateOf("") }

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
                    TransactionCard(transaction)
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
fun TransactionCard(transaction: TransactionF) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${if (transaction.isPositive) "+" else "-"} €${transaction.value}",
                color = if (transaction.isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Text(text = transaction.date, style = MaterialTheme.typography.bodySmall)
            Text(text = transaction.category, style = MaterialTheme.typography.bodySmall)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateFilterSection(viewModel: TransactionViewModel) {
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