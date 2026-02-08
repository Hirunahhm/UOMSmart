package com.example.uomsmart.data.models

data class Order(
        val id: String = "",
        val userId: String = "",
        val mealId: String = "",
        val mealName: String = "",
        val price: Double = 0.0,
        val imageUrl: String? = null,
        val status: String = "PENDING", // PENDING, READY, COMPLETED, CANCELLED
        val token: String = "",
        val timestamp: Long = System.currentTimeMillis()
)
