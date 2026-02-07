package com.example.uomsmart.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uomsmart.data.models.Transaction
import com.example.uomsmart.data.repository.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {
    private val transactionRepository = TransactionRepository()
    private val auth = FirebaseAuth.getInstance()

    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            transactionRepository
                    .getUserTransactions(userId)
                    .onSuccess { result -> transactions = result }
                    .onFailure {
                        // Handle error
                    }
            isLoading = false
        }
    }
}
