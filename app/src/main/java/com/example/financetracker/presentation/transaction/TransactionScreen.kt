package com.example.financetracker.presentation.transaction

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financetracker.MainActivity
import com.example.financetracker.database.model.Category
import com.example.financetracker.database.model.CurrencyType
import com.example.financetracker.database.model.Transaction
import com.example.financetracker.viewmodel.GlobalViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier,
    navController: NavController,
) {
    val viewModel = MainActivity.globalViewModel
    val transactions by viewModel.userTransactions.observeAsState(initial = emptyList())
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val categories by viewModel.userCategories.observeAsState(emptyList())
    val selectedFilter by viewModel.filter.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    var expandedCategorySelector by remember { mutableStateOf(false) }

    val activeUser by viewModel.activeUser.collectAsState()
    val preferredCurrencyType = activeUser?.preferredCurrency

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Transactions") }, actions = {
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
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Type Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Box(modifier = Modifier.padding(16.dp)) {
                    Button(onClick = { expanded = true }) {
                        Text("Type: $selectedFilter")
                    }
                    DropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("All", "Budget", "Cost").forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = {
                                viewModel.setFilter(option)
                                expanded = false
                            })
                        }
                    }
                }

                Box(modifier = Modifier.padding(16.dp)) {
                    Button(onClick = { expandedCategorySelector = true }) {
                        Text("Category: $selectedFilter") //TODO: TEXT TO BE A CATEGORY, NOT TYPE
                    }
                    DropdownMenu(
                        expanded = expandedCategorySelector,
                        onDismissRequest = { expandedCategorySelector = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Categories") },
                            onClick = {
                                viewModel.setSelectedCategoryId(null)
                                expandedCategorySelector = false
                            }
                        )
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    viewModel.setSelectedCategoryId(category.id)
                                    expandedCategorySelector = false
                                }
                            )
                        }
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
                items(filteredTransactions) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        categories = categories,
                        preferredCurrencyType = preferredCurrencyType,
                        onDelete = { viewModel.deleteTransaction(it) },
                        onEdit = {
                            selectedTransaction = it
                            showEditDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (showCreateDialog && activeUser?.id != null) {
                val userId by viewModel.activeUserId.collectAsState()
                val categories by viewModel.userCategories.observeAsState(emptyList())

                if (userId != null) {
                    CreateOrEditTransactionDialog(
                        transaction = null,
                        categories = categories,
                        onDismiss = { showCreateDialog = false },
                        onSubmit = {
                            viewModel.insertTransaction(it)
                            showCreateDialog = false
                        },
                        userId = userId!!
                    )
                }
            }

            if (showEditDialog && selectedTransaction != null) {
                EditTransactionDialog(
                    transaction = selectedTransaction!!,
                    categories = categories,
                    onDismiss = { showEditDialog = false },
                    onSave = {
                        viewModel.updateTransaction(it)
                        showEditDialog = false
                    }
                )
            }

        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionCard(
    transaction: Transaction,
    categories: List<Category>,
    preferredCurrencyType: CurrencyType?,
    onDelete: (Transaction) -> Unit,
    onEdit: (Transaction) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val localDate = transaction.timestamp.atZone(ZoneId.systemDefault()).toLocalDate()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${if (transaction.isPositive) "+" else "-"} ${preferredCurrencyType?.symbol ?: ""}${transaction.amount}",
                    color = if (transaction.isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )

                val categoryName =
                    categories.find { it.id == transaction.categoryId }?.name ?: "Unknown"
                Text(text = categoryName, style = MaterialTheme.typography.bodySmall)

                Text(
                    text = "Date: ${localDate.format(formatter)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { onEdit(transaction) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit transaction",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { onDelete(transaction) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete transaction",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    var valueInput by remember { mutableStateOf(transaction.amount.toString()) }
    var dateInput by remember {
        mutableStateOf(
            transaction.timestamp.atZone(ZoneId.systemDefault()).toLocalDate()
                .format(DateTimeFormatter.ISO_DATE)
        )
    }
    var selectedCategoryId by remember { mutableStateOf(transaction.categoryId) }
    var isPositive by remember { mutableStateOf(transaction.isPositive) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Transaction") },
        confirmButton = {
            TextButton(onClick = {
                val updated = transaction.copy(
                    amount = valueInput.toDoubleOrNull() ?: transaction.amount,
                    timestamp = LocalDate.parse(dateInput).atStartOfDay(ZoneId.systemDefault())
                        .toInstant(),
                    categoryId = selectedCategoryId,
                    isPositive = isPositive
                )
                onSave(updated)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = valueInput,
                    onValueChange = { valueInput = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                DatePickerField("Date", dateInput, onDateSelected = { dateInput = it })

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = categories.find { it.id == selectedCategoryId }?.name ?: "",
                    onValueChange = {},
                    label = { Text("Category") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
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
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategoryId = category.id
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row {
                    Text("Type:")
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = isPositive,
                        onClick = { isPositive = true },
                        label = { Text("Budget") })
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = !isPositive,
                        onClick = { isPositive = false },
                        label = { Text("Cost") })
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateOrEditTransactionDialog(
    transaction: Transaction? = null, // null means create mode
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSubmit: (Transaction) -> Unit,
    userId: Int
) {
    val isEdit = transaction != null

    var valueInput by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var dateInput by remember {
        mutableStateOf(
            transaction?.timestamp?.atZone(ZoneId.systemDefault())?.toLocalDate()
                ?.format(DateTimeFormatter.ISO_DATE) ?: LocalDate.now()
                .format(DateTimeFormatter.ISO_DATE)
        )
    }
    var selectedCategoryId by remember {
        mutableStateOf(
            transaction?.categoryId ?: categories.firstOrNull()?.id ?: -1
        )
    }
    var isPositive by remember { mutableStateOf(transaction?.isPositive ?: true) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Edit Transaction" else "Add Transaction") },
        confirmButton = {
            TextButton(onClick = {
                val amount = valueInput.toDoubleOrNull() ?: return@TextButton
                val date = try {
                    LocalDate.parse(dateInput)
                } catch (e: Exception) {
                    return@TextButton
                }
                val instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                val newTransaction = Transaction(
                    id = transaction?.id ?: 0,
                    amount = amount,
                    timestamp = instant,
                    categoryId = selectedCategoryId,
                    isPositive = isPositive,
                    userId = transaction?.userId ?: userId
                )
                onSubmit(newTransaction)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            Column {
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

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Type:")
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = isPositive,
                        onClick = { isPositive = true },
                        label = { Text("Budget") })
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = !isPositive,
                        onClick = { isPositive = false },
                        label = { Text("Cost") })
                }
            }
        }
    )
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
                onClick = { showStartPicker = true }, modifier = Modifier.weight(1f)
            ) {
                Text("Start: ${startDate?.format(formatter) ?: "Select"}")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { showEndPicker = true }, modifier = Modifier.weight(1f)
            ) {
                Text("End: ${endDate?.format(formatter) ?: "Select"}")
            }
        }
    }

    if (showStartPicker) {
        DatePickerDialog(
            initialDate = startDate?.toString() ?: "",
            onDismiss = { showStartPicker = false },
            onDateSelected = {
                viewModel.setStartDate(LocalDate.parse(it))
                showStartPicker = false
            }
        )
    }

    if (showEndPicker) {
        DatePickerDialog(
            initialDate = endDate?.toString() ?: "",
            onDismiss = { showEndPicker = false },
            onDateSelected = {
                viewModel.setEndDate(LocalDate.parse(it))
                showEndPicker = false
            }
        )
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
