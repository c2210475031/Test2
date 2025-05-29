package com.example.financetracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.financetracker.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryViewModel : ViewModel() {
    private val _categories = MutableStateFlow(
        listOf(
            Category("Groceries", 1, 150.0),
            Category("Entertainment", 2, 100.0),
            Category("Utilities", 3, -1.0),
            Category("Rent", 4, 800.0),
            Category("Transport", 5, -1.0)
        )
    )
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
}
