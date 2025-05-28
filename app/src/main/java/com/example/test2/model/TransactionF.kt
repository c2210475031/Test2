package com.example.test2.model

data class TransactionF(
    val value: Double,
    val date: String,
    val category: String,
    val categoryID: String,
    val isPositive: Boolean,
    val ID: Int = 0
)