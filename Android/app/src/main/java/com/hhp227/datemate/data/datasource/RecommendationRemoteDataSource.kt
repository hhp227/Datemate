package com.hhp227.datemate.data.datasource

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.TimeUnit

class RecommendationRemoteDataSource private constructor(
    private val firestore: FirebaseFirestore
) {
    private fun dailyRef(userId: String, date: String) =
        firestore.collection("recommendations")
            .document(userId)
            .collection("daily")
            .document(date)

    suspend fun fetchTodayRecommendedIds(userId: String, today: String): List<String> {
        val todayDoc = dailyRef(userId, today).get().await()

        if (todayDoc.exists() && todayDoc.get("profileIds") != null) {
            val ids = todayDoc.get("profileIds") as List<String>
            return ids
        } else {
            return emptyList()
        }
    }

    suspend fun fetchTodayChoice(userId: String, today: String): Map<*, *> {
        val todayDoc = dailyRef(userId, today).get().await()
        return if (todayDoc.exists() && todayDoc.get("choices") != null) todayDoc.get("choices") as Map<*, *>
        else emptyMap<String, String>()

    }

    suspend fun loadExcludedProfileIds(userId: String): Set<String> {
        val weekAgo = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(MIN_EXCLUSION_DAYS)))
        val docs = firestore.collection("recommendations")
            .document(userId)
            .collection("daily")
            .whereGreaterThan("createdAt", weekAgo)
            .get()
            .await()
        val result = mutableSetOf<String>()

        for (doc in docs.documents) {
            val recommendIds = doc.get("profileIds") as? List<String> ?: emptyList()
            val choices = doc.get("choices") as? Map<*, *>

            result.addAll(recommendIds)
            choices?.get("left")?.let { result.add(it as String) }
            choices?.get("right")?.let { result.add(it as String) }
            choices?.get("selected")?.let { result.add(it as String) }
        }
        return result
    }

    suspend fun saveDailyRecommendations(userId: String, recommendedUids: List<String>, today: String) {
        val docRef = dailyRef(userId, today)

        val data = mapOf(
            "profileIds" to recommendedUids,
            "createdAt" to Timestamp.now(),
        )
        docRef.set(data, SetOptions.merge()).await()
    }

    suspend fun saveTodayChoice(userId: String, left: String, right: String, today: String) {
        val data = mapOf(
            "choices" to mapOf(
                "left" to left,
                "right" to right,
                "selected" to null
            ),
            "createdAt" to Timestamp.now()
        )

        dailyRef(userId, today).set(data, SetOptions.merge()).await()
    }

    suspend fun selectTodayChoice(userId: String, selectedId: String, today: String) {
        dailyRef(userId, today)
            .update("choices.selected", selectedId)
            .await()
    }

    companion object {
        const val MIN_EXCLUSION_DAYS = 7L

        @Volatile private var instance: RecommendationRemoteDataSource? = null

        fun getInstance(firestore: FirebaseFirestore) =
            instance ?: synchronized(this) {
                instance ?: RecommendationRemoteDataSource(firestore).also { instance = it }
            }
    }
}