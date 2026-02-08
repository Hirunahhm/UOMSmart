package com.example.uomsmart.data.models

data class Occupancy(
        val id: String = "",
        val location: String = "",
        val currentPercentage: Int = 0 // 0-100
)
