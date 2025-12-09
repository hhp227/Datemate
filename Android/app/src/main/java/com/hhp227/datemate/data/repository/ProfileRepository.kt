package com.hhp227.datemate.data.repository

import android.net.Uri
import com.hhp227.datemate.common.Resource
import com.hhp227.datemate.data.datasource.ProfileRemoteDataSource
import com.hhp227.datemate.data.model.Gender
import com.hhp227.datemate.data.model.Profile
import com.hhp227.datemate.data.model.UserCache
import kotlinx.coroutines.flow.*

class ProfileRepository private constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource,
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) {
    private val profileCache = mutableMapOf<String, Profile>()

    fun updateUserProfile(
        userId: String,
        name: String,
        gender: String,
        birthday: Long,
        job: String,
        bio: String,
        country: String,
        imageUris: List<Uri>
    ): Flow<Resource<Boolean>> = storageRepository.uploadAllImagesResultStream(imageUris, userId).flatMapConcat { result ->
        when(result) {
            is Resource.Loading -> flowOf(Resource.Loading())
            is Resource.Error -> flowOf(Resource.Error(result.message ?: "이미지 업로드 실패"))
            is Resource.Success -> {
                val uploadedUrls = result.data ?: emptyList()

                flow {
                    val authResult = userRepository.updateUserProfile(name, uploadedUrls).first()

                    if (authResult is Resource.Error)
                        emit(Resource.Error(authResult.message ?: "Auth 업데이트 실패"))
                    else {
                        val firestoreUid = profileRemoteDataSource.updateUserProfile(
                            userId,
                            name,
                            gender,
                            birthday,
                            bio,
                            job,
                            country,
                            uploadedUrls
                        )

                        userRepository.storeUserProfile(UserCache(firestoreUid))
                        emit(Resource.Success(true))
                    }
                }
            }
        }
    }
        .onStart { emit(Resource.Loading()) }

    suspend fun getProfile(userId: String): Profile? {
        profileCache[userId]?.let { return it }
        return try {
            val profile = profileRemoteDataSource.getProfile(userId)

            if (profile != null) profileCache[userId] = profile
            profile
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getProfiles(ids: List<String>): List<Profile> {
        val result = mutableListOf<Profile>()

        for (id in ids) {
            val profile = getProfile(id)

            if (profile != null) result.add(profile)
        }
        return result
    }

    suspend fun getRandomProfiles(gender: Gender, randomStart: Double, limit: Long): List<Profile> {
        return profileRemoteDataSource.fetchRandomCandidates(gender, randomStart, limit)
    }

    suspend fun getPopularProfiles(gender: Gender): List<Profile> {
        return profileRemoteDataSource.fetchPopularCandidates(gender)
    }

    suspend fun getNewMemberProfiles(gender: Gender): List<Profile> {
        return profileRemoteDataSource.fetchNewUserCandidates(gender)
    }

    suspend fun getRecentActiveProfiles(gender: Gender): List<Profile> {
        return profileRemoteDataSource.fetchRecentActiveCandidates(gender)
    }

    suspend fun getGlobalProfiles(gender: Gender, randomStart: Double, country: String?): List<Profile> {
        return profileRemoteDataSource.fetchGlobalCandidates(gender, randomStart, country)
    }

    suspend fun rateProfile(targetUid: String, raterUid: String, score: Double) {
        profileRemoteDataSource.rateProfile(targetUid, raterUid, score)
        profileCache.remove(targetUid)
    }

    companion object {
        @Volatile private var instance: ProfileRepository? = null

        fun getInstance(
            profileRemoteDataSource: ProfileRemoteDataSource,
            userRepository: UserRepository,
            storageRepository: StorageRepository
        ) =
            instance ?: synchronized(this) {
                instance ?: ProfileRepository(
                    profileRemoteDataSource,
                    userRepository,
                    storageRepository
                ).also { instance = it }
            }
    }
}