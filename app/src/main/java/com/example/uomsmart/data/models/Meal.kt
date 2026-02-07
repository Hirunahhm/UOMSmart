package com.example.uomsmart.data.models

data class Meal(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageRes: Int? = null
)
