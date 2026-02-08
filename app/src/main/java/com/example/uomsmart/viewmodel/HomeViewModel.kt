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
    private val userRepository = com.example.uomsmart.data.repository.UserRepository()
    private val transactionRepository = com.example.uomsmart.data.repository.TransactionRepository()
    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    var occupancies by mutableStateOf<List<Occupancy>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var walletBalance by mutableStateOf(0.0)
        private set

    init {
        loadData()
        loadWalletBalance()
    }

    fun loadWalletBalance() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.getWalletBalance(userId).onSuccess { balance -> walletBalance = balance }
        }
    }

    fun topUpWallet(amount: Double) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            userRepository
                    .topUpWallet(userId, amount)
                    .onSuccess { newBalance ->
                        walletBalance = newBalance
                        // Create Transaction Record
                        val transaction =
                                com.example.uomsmart.data.models.Transaction(
                                        userId = userId,
                                        amount = amount,
                                        type = "CREDIT",
                                        description = "Top Up"
                                )
                        transactionRepository.createTransaction(transaction)
                    }
                    .onFailure { errorMessage = "Top Up Failed: ${it.message}" }
            isLoading = false
        }
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
