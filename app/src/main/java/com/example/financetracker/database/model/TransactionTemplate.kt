package com.example.financetracker.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_templates")
data class TransactionTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Double,
    val isPositive: Boolean,
    val categoryId: Int,
    val userId: Int
)