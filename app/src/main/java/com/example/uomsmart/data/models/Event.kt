package com.example.uomsmart.data.models

data class Event(
    val id: String,
    val title: String,
    val date: String,
    val location: String,
    val imageUrl: String? = null
)
