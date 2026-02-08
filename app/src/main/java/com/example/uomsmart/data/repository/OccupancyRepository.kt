package com.example.uomsmart.data.repository

import com.example.uomsmart.data.models.Occupancy
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OccupancyRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val occupanciesCollection = firestore.collection("occupancies")

    suspend fun getOccupancies(): Result<List<Occupancy>> {
        return try {
            val snapshot = occupanciesCollection.get().await()
            val occupancies =
                    snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Occupancy::class.java)?.copy(id = doc.id)
                    }

            if (occupancies.isEmpty()) {
                seedOccupancies()
                val newSnapshot = occupanciesCollection.get().await()
                val newOccupancies =
                        newSnapshot.documents.mapNotNull { doc ->
                            doc.toObject(Occupancy::class.java)?.copy(id = doc.id)
                        }
                Result.success(newOccupancies)
            } else {
                Result.success(occupancies)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun seedOccupancies() {
        val defaultOccupancies =
                listOf(
                        Occupancy(location = "Main Library", currentPercentage = 75),
                        Occupancy(location = "Engineering Labs", currentPercentage = 40),
                        Occupancy(location = "Student Lounge", currentPercentage = 90),
                        Occupancy(location = "Canteen Area", currentPercentage = 65)
                )

        defaultOccupancies.forEach { occupancy ->
            try {
                occupanciesCollection.add(occupancy).await()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
