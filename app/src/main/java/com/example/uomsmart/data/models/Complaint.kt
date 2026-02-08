package com.example.uomsmart.data.models

data class Complaint(
        val id: String = "",
        val userId: String = "",
        val category: String = "",
        val description: String = "",
        val urgency: Int = 1,
        val status: String = "Open",
        val timestamp: Long = System.currentTimeMillis()
)
