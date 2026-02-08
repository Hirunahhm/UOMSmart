package com.example.uomsmart.data.repository

import com.example.uomsmart.data.models.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OrderRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")

    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val docRef = ordersCollection.document()
            val newOrder = order.copy(id = docRef.id)
            docRef.set(newOrder).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserOrders(userId: String): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection.whereEqualTo("userId", userId).get().await()

            val orders =
                    snapshot.documents
                            .mapNotNull { doc -> doc.toObject(Order::class.java) }
                            .sortedByDescending { it.timestamp }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
