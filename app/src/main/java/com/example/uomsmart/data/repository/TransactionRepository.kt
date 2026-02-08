package com.example.uomsmart.data.repository

import com.example.uomsmart.data.models.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TransactionRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val transactionsCollection = firestore.collection("transactions")

    suspend fun createTransaction(transaction: Transaction): Result<String> {
        return try {
            val docRef = transactionsCollection.document()
            val newTransaction = transaction.copy(id = docRef.id)
            docRef.set(newTransaction).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserTransactions(userId: String): Result<List<Transaction>> {
        return try {
            val snapshot = transactionsCollection.whereEqualTo("userId", userId).get().await()

            val transactions =
                    snapshot.documents
                            .mapNotNull { doc -> doc.toObject(Transaction::class.java) }
                            .sortedByDescending { it.timestamp }
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
