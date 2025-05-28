package com.example.test2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(onContinue: () -> Unit, onCategories: () -> Unit, onAddTransaction: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Welcome") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to Finance Tracker", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onContinue) {
                Text("View Transactions")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onCategories) {
                Text("View Categories")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onAddTransaction) {
                Text("Add Transaction")
            }

        }

    }
}
