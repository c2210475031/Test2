package com.example.financetracker.model

data class TransactionF(
    val value: Double,
    val date: String,
    val category: String,
    val isPositive: Boolean,
    val ID: Int = 0
)