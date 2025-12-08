package com.hhp227.datemate.data.datasource

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hhp227.datemate.data.model.Match
import kotlinx.coroutines.tasks.await

class MatchRemoteDataSource private constructor(
    private val firestore: FirebaseFirestore
) {
    private fun matchId(a: String, b: String): String {
        return listOf(a, b).sorted().joinToString("_")
    }

    suspend fun createMatch(userId: String, selectedId: String) {
        val id = matchId(userId, selectedId)

        val matchDoc = mapOf(
            "user1" to userId,
            "user2" to selectedId,
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("matches")
            .document(id)
            .set(matchDoc)
            .await()
    }

    suspend fun getMatches(userId: String): List<Match> {
        val snapshot1 = firestore.collection("matches")
            .whereEqualTo("user1", userId)
            .get()
            .await()

        val snapshot2 = firestore.collection("matches")
            .whereEqualTo("user2", userId)
            .get()
            .await()

        return (snapshot1.toObjects(Match::class.java) +
                snapshot2.toObjects(Match::class.java))
    }

    companion object {
        @Volatile private var instance: MatchRemoteDataSource? = null

        fun getInstance(firestore: FirebaseFirestore) =
            instance ?: synchronized(this) {
                instance ?: MatchRemoteDataSource(firestore).also { instance = it }
            }
    }
}