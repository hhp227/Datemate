package com.hhp227.datemate.data.datasource

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.hhp227.datemate.data.model.Gender
import com.hhp227.datemate.data.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ProfileRemoteDataSource private constructor(
    private val firestore: FirebaseFirestore
) {
    fun getTodayRecommendations(userId: String, limit: Int = 5): Flow<List<Profile>> = flow {
        val today = getTodayKey()
        val document = firestore.collection("recommendations")
            .document(userId)
            .collection("daily")
            .document(today)
            .get()
            .await()

        if (document.exists()) {
            val profiles = (document.get("profileIds") as? List<String>)?.mapNotNull {
                firestore.collection("profiles").document(it).get().await().toObject(Profile::class.java)?.apply { uid = it }
            } ?: emptyList()

            emit(profiles)
        } else {
            // 오늘 추천 없으면 새로 추천 생성
            val excludedIds = loadExcludedProfileIds(userId)
            val candidates = loadCandidateProfiles(userId, excludedIds)
            val chosen = candidates.shuffled().take(limit)

            recordRecommendations(userId, chosen.map(Profile::uid), today)
            emit(chosen)
        }
    }

    private suspend fun recordRecommendations(userId: String, recommendedUids: List<String>, today: String) {
        val batch = firestore.batch()
        val currentTimestamp = Timestamp.now()
        val newDocRef = firestore.collection("recommendations")
            .document(userId)
            .collection("daily")
            .document(today)
        val record = mapOf(
            "profileIds" to recommendedUids,
            "createdAt" to currentTimestamp
        )

        batch.set(newDocRef, record)
        batch.commit().await()
    }

    private suspend fun loadExcludedProfileIds(userId: String): Set<String> {
        val weekAgo = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(MIN_EXCLUSION_DAYS)))
        val docs = firestore.collection("recommendations")
            .document(userId)
            .collection("daily")
            .whereGreaterThan("createdAt", weekAgo)
            .get()
            .await()
        return docs.documents
            .flatMap { it.get("profileIds") as? List<String> ?: emptyList() }
            .toSet()
    }

    private suspend fun loadCandidateProfiles(userId: String, excludedIds: Set<String>): List<Profile> {
        val myProfile = firestore.collection("profiles")
            .document(userId)
            .get()
            .await()
            .toObject(Profile::class.java)
        val gender = if (myProfile?.gender == Gender.MALE.name) Gender.FEMALE else Gender.MALE
        val snapshot = firestore.collection("profiles")
            .whereEqualTo("gender", gender.name) // 이성만 조회
            .limit(100)
            .get()
            .await()
        return snapshot.documents.mapNotNull { document ->
            val profile = document.toObject(Profile::class.java) ?: return@mapNotNull null
            profile.uid = document.id
            if (document.id != userId && !excludedIds.contains(document.id)) profile else null
        }
    }

    private fun getTodayKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    companion object {
        const val MIN_EXCLUSION_DAYS = 7L

        @Volatile private var instance: ProfileRemoteDataSource? = null

        fun getInstance(firestore: FirebaseFirestore) =
            instance ?: synchronized(this) {
                instance ?: ProfileRemoteDataSource(firestore).also { instance = it }
            }
    }
}