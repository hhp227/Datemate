package com.hhp227.datemate.data.repository

import com.google.firebase.Timestamp
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.datasource.RecommendationRemoteDataSource
import com.hhp227.datemate.data.model.Gender
import com.hhp227.datemate.data.model.Profile
import com.hhp227.datemate.data.model.RecommendationResult
import com.hhp227.datemate.data.model.TodayChoice
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class RecommendationRepository private constructor(
    private val recommendationRemoteDataSource: RecommendationRemoteDataSource,
    private val profileRepository: ProfileRepository
) {
    fun getTodayKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private suspend fun getRecommendationCandidates(userId: String, fetchLimit: Long = 300): List<Profile> {
        val excluded = recommendationRemoteDataSource.loadExcludedProfileIds(userId)
        val myProfile = profileRepository.getProfile(userId)
        val targetGender = if (myProfile?.gender == Gender.MALE.name) Gender.FEMALE else Gender.MALE
        val randomStart = Math.random()
        val rawCandidates = profileRepository.getRandomProfiles(targetGender, randomStart, fetchLimit)
        return rawCandidates.filter { p ->
            p.uid != userId && !excluded.contains(p.uid)
        }
    }

    private suspend fun getTodayRecommendations(userId: String, limit: Int = 5): List<Profile> {
        val today = getTodayKey()
        val todayDoc = recommendationRemoteDataSource.dailyRef(userId, today).get().await()

        if (todayDoc.exists() && todayDoc.get("profileIds") != null) {
            val ids = todayDoc.get("profileIds") as List<String>
            val profiles = profileRepository.getProfiles(ids)
            return profiles
        } else {
            val candidates = getRecommendationCandidates(userId).shuffled().take(limit)

            recommendationRemoteDataSource.saveDailyRecommendations(userId, candidates.map { it.uid }, today)
            return candidates
        }
    }

    private suspend fun getTodayChoice(userId: String): TodayChoice {
        val today = getTodayKey()
        val todayDoc = recommendationRemoteDataSource.dailyRef(userId, today).get().await()

        if (todayDoc.exists() && todayDoc.get("choices") != null) {
            val c = todayDoc.get("choices") as Map<*, *>
            val result = TodayChoice(
                left = (c["left"] as? String)?.let { profileRepository.getProfile(it) },
                right = (c["right"] as? String)?.let { profileRepository.getProfile(it) },
                selected = (c["selected"] as? String)?.let { profileRepository.getProfile(it) }
            )
            return result
        } else {
            val candidates = getRecommendationCandidates(userId, 50).shuffled().take(2)

            if (candidates.size < 2) {
                return TodayChoice(null, null, null)
            } else {
                val result = TodayChoice(candidates[0], candidates[1], null)

                recommendationRemoteDataSource.saveTodayChoice(userId, candidates[0].uid, candidates[1].uid, today)
                return result
            }
        }
    }

    private suspend fun getPopularRecommendations(userId: String): List<Profile> {
        val myProfile = profileRepository.getProfile(userId)
        val targetGender = if (myProfile?.gender == Gender.MALE.name) Gender.FEMALE else Gender.MALE
        return profileRepository.getPopularProfiles(targetGender)
    }

    private suspend fun getNewMemberRecommendations(userId: String): List<Profile> {
        val myProfile = profileRepository.getProfile(userId)
        val targetGender = if (myProfile?.gender == Gender.MALE.name) Gender.FEMALE else Gender.MALE
        return profileRepository.getNewMemberProfiles(targetGender)
    }

    private suspend fun getRecentActiveRecommendations(userId: String): List<Profile> {
        val myProfile = profileRepository.getProfile(userId)
        val targetGender = if (myProfile?.gender == Gender.MALE.name) Gender.FEMALE else Gender.MALE
        return profileRepository.getRecentActiveProfiles(targetGender)
    }

    private suspend fun getGlobalRecommendations(userId: String): List<Profile> {
        val myProfile = profileRepository.getProfile(userId)
        val targetGender = if (myProfile?.gender == Gender.MALE.name) Gender.FEMALE else Gender.MALE
        val country = myProfile?.country
        val randomStart = Math.random()
        return profileRepository.getGlobalProfiles(targetGender, randomStart, country)
    }

    private suspend fun getMockRecommendations(): List<Profile> {
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
        return DummyUsers.shuffled()
    }

    fun getAllRecommendationsResultStream(userId: String): Flow<Resource<RecommendationResult>> {
        return flow {
            try {
                val result = coroutineScope {
                    val todayRecommendationsDeferred = async { getTodayRecommendations(userId) }
                    val todayChoiceDeferred = async { getTodayChoice(userId) }
                    val popularRecommendationsDeferred = async { getPopularRecommendations(userId) }
                    val newMemberRecommendationsDeferred = async { getNewMemberRecommendations(userId) }
                    val globalRecommendationsDeferred = async { getGlobalRecommendations(userId) }
                    val recentActiveRecommendationsDeferred = async { getRecentActiveRecommendations(userId) }
                    return@coroutineScope RecommendationResult(
                        todayRecommendationsDeferred.await(),
                        todayChoiceDeferred.await(),
                        popularRecommendationsDeferred.await(),
                        newMemberRecommendationsDeferred.await(),
                        globalRecommendationsDeferred.await(),
                        recentActiveRecommendationsDeferred.await()
                    )
                }

                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "테마 추천 로딩 실패"))
            }
        }
            .onStart { emit(Resource.Loading()) }
    }

    fun selectTodayChoiceResultStream(userId: String, selectedId: String): Flow<Resource<Unit>> {
        return flow {
            try {
                val today = getTodayKey()

                recommendationRemoteDataSource.selectTodayChoice(userId, selectedId, today)
                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "오늘 선택 저장 실패"))
            }
        }
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