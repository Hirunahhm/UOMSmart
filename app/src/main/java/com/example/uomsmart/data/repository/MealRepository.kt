package com.example.uomsmart.data.repository

import com.example.uomsmart.data.models.Meal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Booking(
    val id: String = "",
    val userId: String = "",
    val mealId: String = "",
    val mealName: String = "",
    val token: String = "",
    val amount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending" // pending, collected, cancelled
)

class MealRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mealsCollection = firestore.collection("meals")
    private val bookingsCollection = firestore.collection("bookings")
    
    suspend fun getMeals(): Result<List<Meal>> {
        return try {
            val snapshot = mealsCollection.get().await()
            val meals = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Meal::class.java)?.copy(id = doc.id)
            }
            
            if (meals.isEmpty()) {
                seedMeals()
                // Fetch again after seeding
                val newSnapshot = mealsCollection.get().await()
                val newMeals = newSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Meal::class.java)?.copy(id = doc.id)
                }
                Result.success(newMeals)
            } else {
                Result.success(meals)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun seedMeals() {
        val defaultMeals = listOf(
            Meal(name = "Chicken Fried Rice", price = 450.0, description = "Spicy fried rice with chicken and chili paste", imageUrl = "https://example.com/fried_rice.jpg", category = "Lunch", available = true),
            Meal(name = "Vegetable Kottu", price = 350.0, description = "Hot and spicy vegetable kottu with cheese", imageUrl = "https://example.com/kottu.jpg", category = "Dinner", available = true),
            Meal(name = "Rice and Curry (Fish)", price = 250.0, description = "Traditional rice and curry with fish", imageUrl = "https://example.com/rice_curry.jpg", category = "Lunch", available = true),
            Meal(name = "Chicken Burger", price = 500.0, description = "Crispy chicken burger with fries", imageUrl = "https://example.com/burger.jpg", category = "Snacks", available = true),
            Meal(name = "Iced Coffee", price = 150.0, description = "Chilled coffee with milk", imageUrl = "https://example.com/coffee.jpg", category = "Beverages", available = true)
        )
        
        defaultMeals.forEach { meal ->
            try {
                mealsCollection.add(meal).await()
            } catch (e: Exception) {
                // Log error or ignore
            }
        }
    }
    
    suspend fun bookMeal(userId: String, meal: Meal): Result<Booking> {
        return try {
            val token = generateToken()
            val booking = Booking(
                userId = userId,
                mealId = meal.id,
                mealName = meal.name,
                token = token,
                amount = meal.price,
                timestamp = System.currentTimeMillis(),
                status = "pending"
            )
            
            val docRef = bookingsCollection.add(booking).await()
            Result.success(booking.copy(id = docRef.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBookingHistory(userId: String): Result<List<Booking>> {
        return try {
            val snapshot = bookingsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val bookings = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Booking::class.java)?.copy(id = doc.id)
            }
            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateToken(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = (1..6).map { chars.random() }.joinToString("")
        return "UOM-M#$random"
    }
}
