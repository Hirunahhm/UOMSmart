package com.example.uomsmart.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uomsmart.data.models.Meal
import com.example.uomsmart.data.repository.MealRepository
import kotlinx.coroutines.launch

class CanteenViewModel : ViewModel() {
    private val mealRepository = MealRepository()

    var meals by mutableStateOf<List<Meal>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadMeals()
    }

    fun loadMeals() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            mealRepository.getMeals()
                .onSuccess { fetchedMeals ->
                    meals = fetchedMeals
                }
                .onFailure { e ->
                    errorMessage = e.message ?: "Failed to load meals"
                }
            
            isLoading = false
        }
    }
}
