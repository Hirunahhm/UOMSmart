package com.example.uomsmart.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserProfile(
        val uid: String = "",
        val name: String = "",
        val email: String = "",
        val studentId: String = "",
        val walletBalance: Double = 1000.0, // Starting balance
        val photoUrl: String? = null
)

class UserRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val profile = doc.toObject(UserProfile::class.java)
            profile?.let { Result.success(it.copy(uid = userId)) }
                    ?: Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserProfile(
            userId: String,
            email: String,
            name: String
    ): Result<UserProfile> {
        return try {
            val profile =
                    UserProfile(
                            uid = userId,
                            name = name,
                            email = email,
                            studentId = generateStudentId(),
                            walletBalance = 1000.0 // Initial balance
                    )
            usersCollection.document(userId).set(profile).await()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWalletBalance(userId: String): Result<Double> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val balance = doc.getDouble("walletBalance") ?: 0.0
            Result.success(balance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateWalletBalance(userId: String, newBalance: Double): Result<Double> {
        return try {
            usersCollection.document(userId).update("walletBalance", newBalance).await()
            Result.success(newBalance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deductFromWallet(userId: String, amount: Double): Result<Double> {
        return try {
            val currentBalance = getWalletBalance(userId).getOrThrow()
            if (currentBalance < amount) {
                Result.failure(Exception("Insufficient balance"))
            } else {
                val newBalance = currentBalance - amount
                updateWalletBalance(userId, newBalance)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun topUpWallet(userId: String, amount: Double): Result<Double> {
        return try {
            val currentBalance = getWalletBalance(userId).getOrThrow()
            val newBalance = currentBalance + amount
            updateWalletBalance(userId, newBalance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateStudentId(): String {
        val random = (100000..999999).random()
        return "UOM$random"
    }
}
