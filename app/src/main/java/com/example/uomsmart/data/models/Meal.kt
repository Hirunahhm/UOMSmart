package com.example.uomsmart.data.models

data class Meal(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "General",
    val available: Boolean = true,
    val imageRes: Int? = null
)
