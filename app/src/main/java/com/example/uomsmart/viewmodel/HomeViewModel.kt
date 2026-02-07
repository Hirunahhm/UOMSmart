package com.example.uomsmart.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uomsmart.data.models.Event
import com.example.uomsmart.data.models.Occupancy
import com.example.uomsmart.data.repository.EventRepository
import com.example.uomsmart.data.repository.OccupancyRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val eventRepository = EventRepository()
    private val occupancyRepository = OccupancyRepository()

    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    var occupancies by mutableStateOf<List<Occupancy>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // Fetch both in parallel
                val eventsDeferred = async { eventRepository.getEvents() }
                val occupanciesDeferred = async { occupancyRepository.getOccupancies() }

                val eventsResult = eventsDeferred.await()
                val occupanciesResult = occupanciesDeferred.await()

                eventsResult.onSuccess { events = it }
                occupanciesResult.onSuccess { occupancies = it }

                if (eventsResult.isFailure || occupanciesResult.isFailure) {
                    errorMessage = "Failed to load some data"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }
}
