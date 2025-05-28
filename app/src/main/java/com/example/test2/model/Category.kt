package com.example.test2.model

data class Category (
    val name: String ="Default",
    val id: Int,
    val maxNegativeValue: Double = (-1).toDouble()
)