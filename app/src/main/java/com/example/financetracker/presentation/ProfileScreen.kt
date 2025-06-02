package com.example.financetracker.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.financetracker.MainActivity
import com.example.financetracker.database.model.User
import com.example.financetracker.navigation.Screen
import androidx.compose.runtime.getValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier, navController: NavController) {
    val viewModel = MainActivity.globalViewModel
    val activeUserId by viewModel.activeUserId.collectAsState()
    val users by viewModel.allUsers.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Profile") },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(Screen.StartScreen.route) {
                                popUpTo(Screen.StartScreen.route) { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Home",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        }
    ) { padding ->
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
                    UserCard(user = user, onDelete = {viewModel.deleteUser(user)})
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, onDelete: (User) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = user.name)
            Button(onClick = {onDelete(user)}) {
                Text("Delete")
            }
        }
    }
}