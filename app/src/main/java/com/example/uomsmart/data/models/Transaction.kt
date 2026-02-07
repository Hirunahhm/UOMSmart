package com.example.uomsmart.data.models

data class Transaction(
        val id: String = "",
        val userId: String = "",
        val amount: Double = 0.0,
        val type: String = "DEBIT", // DEBIT, CREDIT
        val description: String = "",
        val timestamp: Long = System.currentTimeMillis()
)
