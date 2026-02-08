package com.example.uomsmart.data.repository

import com.example.uomsmart.data.models.Complaint
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ComplaintRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val complaintsCollection = firestore.collection("complaints")

    suspend fun createComplaint(complaint: Complaint): Result<String> {
        return try {
            val docRef = complaintsCollection.document()
            val newComplaint = complaint.copy(id = docRef.id)
            docRef.set(newComplaint).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
