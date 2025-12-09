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
                        val firestoreUid = profileRemoteDataSource.updateUserProfile(userId, name, gender, birthday, job, bio, uploadedUrls)

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
            val remote = profileRemoteDataSource.getProfile(userId)

            if (remote != null) profileCache[userId] = remote
            remote
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getProfiles(ids: List<String>): List<Profile> {
        val result = mutableListOf<Profile>()

        for (id in ids) {
            val p = getProfile(id)

            if (p != null) result.add(p)
        }
        return result
    }

    suspend fun fetchRandomCandidates(gender: Gender, randomStart: Double, limit: Long): List<Profile> {
        return profileRemoteDataSource.fetchRandomCandidates(gender, randomStart, limit)
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