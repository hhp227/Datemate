package com.hhp227.datemate.data.repository

import com.google.firebase.Timestamp
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.common.asResource
import com.hhp227.datemate.data.datasource.ProfileRemoteDataSource.TodaysChoiceResult
import com.hhp227.datemate.data.datasource.RecommendationRemoteDataSource
import com.hhp227.datemate.data.model.Gender
import com.hhp227.datemate.data.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecommendationRepository private constructor(
    private val recommendationRemoteDataSource: RecommendationRemoteDataSource,
    private val profileRepository: ProfileRepository
) {
    fun getTodayKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    suspend fun getRecommendationCandidates(
        userId: String,
        fetchLimit: Long = 300
    ): List<Profile> {
        val myProfile = profileRepository.getProfile(userId)
        val targetGender =
            if (myProfile?.gender == Gender.MALE.name) Gender.FEMALE else Gender.MALE

        val excluded = recommendationRemoteDataSource.loadExcludedProfileIds(userId)
        val randomStart = Math.random()

        val raw = profileRepository.fetchRandomCandidates(targetGender, randomStart, fetchLimit)

        return raw.filter { p ->
            p.uid != userId && !excluded.contains(p.uid)
        }
    }

    fun getTodayRecommendations(
        userId: String,
        limit: Int = 5
    ): Flow<Resource<List<Profile>>> = flow {
        val today = getTodayKey()
        val todayDoc = recommendationRemoteDataSource.dailyRef(userId, today).get().await()

        if (todayDoc.exists() && todayDoc.get("profileIds") != null) {
            val ids = todayDoc.get("profileIds") as List<String>
            val profiles = ids.mapNotNull { profileRepository.getProfile(it) }
            emit(profiles)
        } else {
            val candidates = getRecommendationCandidates(userId).shuffled().take(limit)

            recommendationRemoteDataSource.saveDailyRecommendations(userId, candidates.map { it.uid }, today)
            emit(candidates)
        }
    }
        .asResource()


    fun getTodaysChoice(userId: String): Flow<Resource<TodaysChoiceResult>> = flow {
        val today = getTodayKey()
        val todayDoc = recommendationRemoteDataSource.dailyRef(userId, today).get().await()

        if (todayDoc.exists() && todayDoc.get("choices") != null) {

            val c = todayDoc.get("choices") as Map<*, *>

            val result = TodaysChoiceResult(
                left = (c["left"] as? String)?.let { profileRepository.getProfile(it) },
                right = (c["right"] as? String)?.let { profileRepository.getProfile(it) },
                selected = (c["selected"] as? String)?.let { profileRepository.getProfile(it) }
            )
            emit(result)
        } else {
            val candidates = getRecommendationCandidates(userId, 50).shuffled().take(2)

            if (candidates.size < 2) {
                emit(TodaysChoiceResult(null, null, null))
            } else {
                val result = TodaysChoiceResult(candidates[0], candidates[1], null)

                recommendationRemoteDataSource.saveTodaysChoice(userId, candidates[0].uid, candidates[1].uid, today)
                emit(result)
            }
        }
    }
        .asResource()

    fun getThemedRecommendations(): Flow<Resource<List<Profile>>> {
        return flow {
            val DummyUser = Profile(
                uid = "1",
                name = "세아",
                birthday = Timestamp.now(),
                job = "UX 디자이너",
                bio = "새로운 인연을 찾고 있어요. 🕺",
                gender = Gender.FEMALE.name,
                photos = listOf("https://picsum.photos/400/600?random=1")
            )
            val DummyUsers = (1..10).map { i ->
                DummyUser.copy(uid = i.toString(), name = "사용자 $i", photos = listOf("https://picsum.photos/400/600?random=$i"))
            }
            emit(DummyUsers)
        }
            .asResource()
    }

    suspend fun selectTodayChoice(userId: String, selectedId: String) {
        val today = getTodayKey()

        recommendationRemoteDataSource.selectTodayChoice(userId, selectedId, today)
    }

    companion object {
        @Volatile private var instance: RecommendationRepository? = null

        fun getInstance(
            recommendationRemoteDataSource: RecommendationRemoteDataSource,
            profileRepository: ProfileRepository
        ) =
            instance ?: synchronized(this) {
                instance ?: RecommendationRepository(
                    recommendationRemoteDataSource,
                    profileRepository
                ).also { instance = it }
            }
    }
}