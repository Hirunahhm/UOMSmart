package com.example.uomsmart.data.repository

import com.example.uomsmart.data.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val eventsCollection = firestore.collection("events")

    suspend fun getEvents(): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection.get().await()
            val events =
                    snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Event::class.java)?.copy(id = doc.id)
                    }

            if (events.isEmpty()) {
                seedEvents()
                val newSnapshot = eventsCollection.get().await()
                val newEvents =
                        newSnapshot.documents.mapNotNull { doc ->
                            doc.toObject(Event::class.java)?.copy(id = doc.id)
                        }
                Result.success(newEvents)
            } else {
                Result.success(events)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun seedEvents() {
        val defaultEvents =
                listOf(
                        Event(
                                title = "Tech Innovation Summit",
                                date = "Nov 15, 2026",
                                location = "University Auditorium",
                                imageUrl = null
                        ),
                        Event(
                                title = "Annual Career Fair",
                                date = "Oct 20, 2026",
                                location = "Sports Complex",
                                imageUrl = null
                        ),
                        Event(
                                title = "Hackathon Kickoff",
                                date = "Dec 05, 2026",
                                location = "CSE Department",
                                imageUrl = null
                        )
                )

        defaultEvents.forEach { event ->
            try {
                eventsCollection.add(event).await()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
