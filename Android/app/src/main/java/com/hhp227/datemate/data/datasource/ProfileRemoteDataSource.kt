package com.hhp227.datemate.data.datasource

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    suspend fun fetchRandomCandidates(gender: Gender, randomStart: Double, limit: Long): List<Profile> {
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

    suspend fun fetchPopularCandidates(gender: Gender): List<Profile> {
        val snapshot = firestore.collection("profiles")
            .whereEqualTo("gender", gender.name)
            // 임의로 아직 ratings를 구현안했으므로 주석 처리
            //.orderBy("ratings", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Profile::class.java)?.apply { uid = doc.id }
        }
    }

    suspend fun fetchNewUserCandidates(gender: Gender): List<Profile> {
        val snapshot = firestore.collection("profiles")
            .whereEqualTo("gender", gender.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Profile::class.java)?.apply { uid = doc.id }
        }
    }

    suspend fun fetchRecentActiveCandidates(gender: Gender): List<Profile> {
        val snapshot = firestore.collection("profiles")
            .whereEqualTo("gender", gender.name)
            // lastActiveAt 필드를 추가할지 고민좀
            // .orderBy("lastActiveAt", Query.Direction.DESCENDING)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Profile::class.java)?.apply { uid = doc.id }
        }
    }

    suspend fun fetchGlobalCandidates(gender: Gender, randomStart: Double, country: String?): List<Profile> {
        val snapshot = firestore.collection("profiles")
            .whereEqualTo("gender", gender.name)
            // 아직 country를 추가안했으므로 주석처리
            //.whereNotEqualTo("country", country)
            .whereGreaterThan("randomKey", randomStart)
            .limit(50)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Profile::class.java)?.apply { uid = doc.id }
        }
    }

    suspend fun updateUserProfile(
        userId: String,
        name: String,
        gender: String,
        birthdayMillis: Long,
        bio: String,
        job: String,
        country: String,
        urls: List<String>?
    ): String {
        val doc = firestore.collection("profiles").document(userId)
        val data = mapOf(
            "name" to name,
            "gender" to gender,
            "birthday" to Timestamp(Date(birthdayMillis)),
            "bio" to bio,
            "job" to job,
            "country" to country,
            "photos" to (urls ?: emptyList()),
            "randomKey" to Math.random(),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        doc.set(data, SetOptions.merge()).await()
        return userId
    }

    suspend fun rateProfile(
        targetUid: String,
        raterUid: String,
        score: Double
    ) {
        val profileRef = firestore.collection("profiles")
            .document(targetUid)

        val ratingRef = profileRef
            .collection("ratings")
            .document(raterUid)

        firestore.runTransaction { tr ->

            // 1) 이미 평가했는지 확인
            val prevRating = tr.get(ratingRef)
            val previousScore = if (prevRating.exists()) prevRating.getDouble("rating") else null

            // 2) 현재 프로필 정보 가져오기
            val profileSnap = tr.get(profileRef)
            val currentRating = profileSnap.getDouble("rating") ?: 0.0
            val ratingCount = profileSnap.getLong("ratingCount") ?: 0

            val newRating: Double
            val newRatingCount: Long

            if (previousScore == null) {
                // 최초 평가
                newRating = ((currentRating * ratingCount) + score) / (ratingCount + 1)
                newRatingCount = ratingCount + 1
            } else {
                // 이전 평점 수정
                newRating = ((currentRating * ratingCount) - previousScore + score) / ratingCount
                newRatingCount = ratingCount
            }

            // 3) 프로필 문서 업데이트
            tr.update(profileRef, mapOf(
                "rating" to newRating,
                "ratingCount" to newRatingCount
            ))

            // 4) rating 기록 저장
            tr.set(ratingRef, mapOf(
                "rating" to score,
                "updatedAt" to FieldValue.serverTimestamp()
            ))

            return@runTransaction null
        }.await()
    }

    companion object {
        @Volatile private var instance: ProfileRemoteDataSource? = null

        fun getInstance(firestore: FirebaseFirestore) =
            instance ?: synchronized(this) {
                instance ?: ProfileRemoteDataSource(firestore).also { instance = it }
            }
    }
}