package com.example.financetracker

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.financetracker.database.AppDatabase
import com.example.financetracker.database.model.User
import com.example.financetracker.database.repository.TransactionRepository
import com.example.financetracker.datastore.UserPreferences
import com.example.financetracker.navigation.Navigation
import com.example.financetracker.ui.theme.FinanceTrackerTheme
import com.example.financetracker.viewmodel.GlobalViewModel
import com.example.financetracker.viewmodel.GlobalViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var globalViewModel: GlobalViewModel
            private set
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = provideRepository()
        globalViewModel = ViewModelProvider(this, GlobalViewModelFactory(repository))
            .get(GlobalViewModel::class.java)

        lifecycleScope.launch {
            val allUsers = globalViewModel.getAllUsersOnce()
            Log.i("MainActivity", "All users: $allUsers")

            if (allUsers.isNotEmpty()) {
                val user = UserPreferences.loadUserId(applicationContext)
                globalViewModel.setActiveUser(applicationContext, user)
            } else {
                val newUser = User(name = "Default User")
                val newUserId = globalViewModel.insertUserAndReturnId(newUser)
                UserPreferences.saveUserId(applicationContext, newUserId)
                globalViewModel.setActiveUser(applicationContext, newUserId)
            }
        }

        setContent {
            FinanceTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun provideRepository(): TransactionRepository {
        val db = AppDatabase.getDatabase(applicationContext)
        return TransactionRepository(
            db.transactionDao(),
            db.categoryDao(),
            db.userDao(),
            db.templateDao()
        )
    }
}