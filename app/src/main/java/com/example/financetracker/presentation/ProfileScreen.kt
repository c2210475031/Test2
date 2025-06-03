package com.example.financetracker.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financetracker.MainActivity
import com.example.financetracker.database.model.CurrencyType
import com.example.financetracker.database.model.User
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier, navController: NavController) {
    val viewModel = MainActivity.globalViewModel
    val activeUserId by viewModel.activeUserId.collectAsState()
    val users by viewModel.allUsers.observeAsState(initial = emptyList())

    var showCreateDialog by remember { mutableStateOf(false) }
    var context = LocalContext.current

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Profile") }, actions = {
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(users) { user ->
                    UserCard(
                        user = user,
                        isActive = user.id == activeUserId,
                        onDelete = { viewModel.deleteUser(user) },
                        onActivate = { viewModel.setActiveUser(context, user.id) })
                }
            }
        }

        if (showCreateDialog) {
            CreateUserDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, currency ->
                    val newUser = User(name = name.trim(), preferredCurrency = currency)
                    viewModel.insertUser(newUser)
                    viewModel.setActiveUser(context, newUser.id)
                    showCreateDialog = false
                })
        }
    }
}

@Composable
fun UserCard(
    user: User,
    isActive: Boolean,
    onDelete: (User) -> Unit,
    onActivate: (User) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = if (isActive)
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        else
            CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Currency: ${user.preferredCurrency}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { onActivate(user) },
                    enabled = !isActive
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Set as active user",
                        tint = if (isActive) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { onDelete(user) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete user",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun CreateUserDialog(
    onDismiss: () -> Unit, onCreate: (String, CurrencyType) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedCurrency by remember { mutableStateOf(CurrencyType.EUR) }

    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onCreate(nameInput, selectedCurrency)
        }) {
            Text("Create")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }, title = { Text("Create New User") }, text = {
        Column {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("User Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Currency dropdown
            Box {
                OutlinedTextField(
                    value = selectedCurrency.toString(),
                    onValueChange = {},
                    label = { Text("Preferred Currency") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true })

                DropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }) {
                    CurrencyType.values().forEach { currency ->
                        DropdownMenuItem(text = { Text(currency.toString()) }, onClick = {
                            selectedCurrency = currency
                            expanded = false
                        })
                    }
                }
            }
        }
    })
}