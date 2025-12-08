package com.hhp227.datemate.data.datasource

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hhp227.datemate.data.model.Gender
import com.hhp227.datemate.data.model.Profile
import kotlinx.coroutines.tasks.await
import java.util.*

class ProfileRemoteDataSource private constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getProfile(uid: String): Profile? =
        firestore.collection("profiles")
            .document(uid)
            .get()
            .await()
            .toObject(Profile::class.java)
            ?.apply { this.uid = uid }

    suspend fun fetchRandomCandidates(
        gender: Gender,
        randomStart: Double,
        limit: Long
    ): List<Profile> {
        // batch 1
        val firstDocs = firestore.collection("profiles")
            .whereEqualTo("gender", gender.name)
            .whereGreaterThan("randomKey", randomStart)
            .limit(limit)
            .get()
            .await()

        val first = firstDocs.documents.mapNotNull { doc ->
            doc.toObject(Profile::class.java)?.apply { uid = doc.id }
        }

        // enough
        if (first.size >= limit) return first.take(limit.toInt())

        // batch 2 (wrap-around)
        val remaining = limit - first.size

        val secondDocs = firestore.collection("profiles")
            .whereEqualTo("gender", gender.name)
            .whereLessThan("randomKey", randomStart)
            .limit(remaining)
            .get()
            .await()

        val second = secondDocs.documents.mapNotNull { doc ->
            doc.toObject(Profile::class.java)?.apply { uid = doc.id }
        }

        return (first + second)
    }

    data class TodaysChoiceResult(
        val left: Profile?,
        val right: Profile?,
        val selected: Profile?
    )

    suspend fun updateUserProfile(
        userId: String,
        name: String,
        gender: String,
        birthdayMillis: Long,
        bio: String,
        job: String,
        urls: List<String>?
    ): String {
        val doc = firestore.collection("profiles").document(userId)
        val data = mapOf(
            "name" to name,
            "gender" to gender,
            "birthday" to Timestamp(Date(birthdayMillis)),
            "bio" to bio,
            "job" to job,
            "photos" to (urls ?: emptyList()),
            "randomKey" to Math.random(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        doc.set(data, SetOptions.merge()).await()
        return userId
    }

    companion object {
        @Volatile private var instance: ProfileRemoteDataSource? = null

        fun getInstance(firestore: FirebaseFirestore) =
            instance ?: synchronized(this) {
                instance ?: ProfileRemoteDataSource(firestore).also { instance = it }
            }
    }
}




/*
class ProfileRemoteDataSource private constructor(
    private val firestore: FirebaseFirestore
) {
    private fun dailyRef(userId: String, date: String) =
        firestore.collection("recommendations")
            .document(userId)
            .collection("daily")
            .document(date)

    private fun getTodayKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private suspend fun getProfile(uid: String): Profile? =
        firestore.collection("profiles")
            .document(uid)
            .get()
            .await()
            .toObject(Profile::class.java)
            ?.apply { this.uid = uid }

    private suspend fun loadExcludedProfileIds(userId: String): Set<String> {
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

    private suspend fun fetchRandomCandidates(
        gender: Gender,
        randomStart: Double,
        limit: Long
    ): List<Profile> {
        // batch 1
        val firstDocs = firestore.collection("profiles")
            .whereEqualTo("gender", gender.name)
            .whereGreaterThan("randomKey", randomStart)
            .limit(limit)
            .get()
            .await()
            .documents

        val first = firstDocs.mapNotNull { doc ->
            doc.toObject(Profile::class.java)?.apply { uid = doc.id }
        }.toMutableList()

        // enough
        if (first.size >= limit) return first.take(limit.toInt())

        // batch 2 (wrap-around)
        val remaining = limit - first.size

        val secondDocs = firestore.collection("profiles")
            .whereEqualTo("gender", gender.name)
            .whereLessThan("randomKey", randomStart)
            .limit(remaining)
            .get()
            .await()
            .documents

        val second = secondDocs.mapNotNull { doc ->
            doc.toObject(Profile::class.java)?.apply { uid = doc.id }
        }

        return (first + second)
    }

    suspend fun getRecommendationCandidates(
        userId: String,
        fetchLimit: Long = 300
    ): List<Profile> {
        val myProfile = getProfile(userId)
        val targetGender =
            if (myProfile?.gender == Gender.MALE.name) Gender.FEMALE else Gender.MALE

        val excluded = loadExcludedProfileIds(userId)
        val randomStart = Math.random()

        // Firestore random sampler
        val rawCandidates = fetchRandomCandidates(targetGender, randomStart, fetchLimit)

        // 필터링: 제외 대상/나 자신 제거
        return rawCandidates.filter { p ->
            p.uid != userId && !excluded.contains(p.uid)
        }
    }

    fun getTodayRecommendations(
        userId: String,
        limit: Int = 5
    ): Flow<List<Profile>> = flow {
        val today = getTodayKey()
        val doc = dailyRef(userId, today).get().await()

        if (doc.exists() && doc.get("profileIds") != null) {
            val ids = doc.get("profileIds") as List<String>
            val profiles = ids.mapNotNull { getProfile(it) }
            emit(profiles)
        } else {
            // 오늘 추천 없으면 새로 생성
            val candidates = getRecommendationCandidates(userId).shuffled().take(limit)

            recordRecommendations(userId, candidates.map { it.uid }, today)
            emit(candidates)
        }
    }

    private suspend fun recordRecommendations(userId: String, recommendedUids: List<String>, today: String) {
        val docRef = dailyRef(userId, today)

        val data = mapOf(
            "profileIds" to recommendedUids,
            "createdAt" to Timestamp.now(),
        )
        docRef.set(data, SetOptions.merge()).await()
    }

    fun getTodaysChoice(userId: String): Flow<TodaysChoiceResult> = flow {
        val today = getTodayKey()
        val ref = dailyRef(userId, today)
        val doc = ref.get().await()

        if (doc.exists() && doc.get("choices") != null) {

            val c = doc.get("choices") as Map<*, *>

            val result = TodaysChoiceResult(
                left = (c["left"] as? String)?.let { getProfile(it) },
                right = (c["right"] as? String)?.let { getProfile(it) },
                selected = (c["selected"] as? String)?.let { getProfile(it) }
            )
            emit(result)
        } else {
            val candidates = getRecommendationCandidates(userId, 50).shuffled().take(2)

            if (candidates.size < 2) {
                emit(TodaysChoiceResult(null, null, null))
            } else {
                val result = TodaysChoiceResult(
                    left = candidates[0],
                    right = candidates[1],
                    selected = null
                )
                val record = mapOf(
                    "choices" to mapOf(
                        "left" to candidates[0].uid,
                        "right" to candidates[1].uid,
                        "selected" to null
                    ),
                    "createdAt" to Timestamp.now()
                )

                ref.set(record, SetOptions.merge()).await()
                emit(result)
            }
        }
    }

    suspend fun selectTodayChoice(userId: String, selectedId: String) {
        val today = getTodayKey()

        dailyRef(userId, today)
            .update("choices.selected", selectedId)
            .await()
    }



















    data class TodaysChoiceResult(
        val left: Profile?,
        val right: Profile?,
        val selected: Profile?
    )

    suspend fun updateUserProfile(
        userId: String,
        name: String,
        gender: String,
        birthdayMillis: Long,
        bio: String,
        job: String,
        urls: List<String>?
    ): String {
        val doc = firestore.collection("profiles").document(userId)
        val data = mapOf(
            "name" to name,
            "gender" to gender,
            "birthday" to Timestamp(Date(birthdayMillis)),
            "bio" to bio,
            "job" to job,
            "photos" to (urls ?: emptyList()),
            "randomKey" to Math.random(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        doc.set(data, SetOptions.merge()).await()
        return userId
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
 */