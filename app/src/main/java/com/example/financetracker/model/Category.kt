package com.example.financetracker.model

data class Category (
    val name: String ="Default",
    val id: Int,
    val maxNegativeValue: Double = (-1).toDouble()
)