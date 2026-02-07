package com.example.uomsmart.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uomsmart.data.models.Meal
import com.example.uomsmart.data.models.Order
import com.example.uomsmart.data.models.Transaction
import com.example.uomsmart.data.repository.MealRepository
import com.example.uomsmart.data.repository.OrderRepository
import com.example.uomsmart.data.repository.TransactionRepository
import com.example.uomsmart.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID
import kotlinx.coroutines.launch

class CanteenViewModel : ViewModel() {
    private val mealRepository = MealRepository()
    private val userRepository = UserRepository()
    private val orderRepository = OrderRepository()
    private val transactionRepository = TransactionRepository()
    private val auth = FirebaseAuth.getInstance()

    var meals by mutableStateOf<List<Meal>>(emptyList())
        private set

    var myOrders by mutableStateOf<List<Order>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var bookingSuccess by mutableStateOf(false)
        private set

    var walletBalance by mutableStateOf(0.0)
        private set

    init {
        loadMeals()
        loadOrders()
        loadWalletBalance()
    }

    fun loadWalletBalance() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.getWalletBalance(userId).onSuccess { balance -> walletBalance = balance }
        }
    }

    fun loadMeals() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            mealRepository
                    .getMeals()
                    .onSuccess { fetchedMeals -> meals = fetchedMeals }
                    .onFailure { e -> errorMessage = e.message ?: "Failed to load meals" }

            isLoading = false
        }
    }

    fun loadOrders() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading = true
            orderRepository
                    .getUserOrders(userId)
                    .onSuccess { orders -> myOrders = orders }
                    .onFailure { e ->
                        // distinct error message for orders if needed, or just log
                        println("Failed to load orders: ${e.message}")
                    }
            isLoading = false
        }
    }

    fun bookMeal(meal: Meal, onResult: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onResult(false, "User not logged in")
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            bookingSuccess = false

            // 1. Check Balance & Deduct
            userRepository
                    .deductFromWallet(userId, meal.price)
                    .onSuccess {
                        // 2. Create Order
                        val token = UUID.randomUUID().toString().substring(0, 8).uppercase()
                        val order =
                                Order(
                                        userId = userId,
                                        mealId = meal.id,
                                        mealName = meal.name,
                                        price = meal.price,
                                        imageUrl = meal.imageUrl,
                                        status = "PENDING",
                                        token = token
                                )

                        orderRepository
                                .createOrder(order)
                                .onSuccess {
                                    // 3. Create Transaction Record
                                    val transaction =
                                            Transaction(
                                                    userId = userId,
                                                    amount = meal.price,
                                                    type = "DEBIT",
                                                    description = "Ordered ${meal.name}"
                                            )
                                    transactionRepository.createTransaction(transaction)

                                    // 4. Update UI
                                    bookingSuccess = true
                                    loadOrders() // Refresh orders list
                                    loadWalletBalance()
                                    onResult(true, null)
                                }
                                .onFailure { e ->
                                    // Refund if order creation fails (optional but good practice)
                                    userRepository.updateWalletBalance(userId, it + meal.price)
                                    errorMessage = "Failed to create order: ${e.message}"
                                    onResult(false, errorMessage)
                                }
                    }
                    .onFailure { e ->
                        errorMessage = "Insufficient Balance or Wallet Error: ${e.message}"
                        onResult(false, errorMessage)
                    }

            isLoading = false
        }
    }

    fun resetBookingStatus() {
        bookingSuccess = false
        errorMessage = null
    }
}
