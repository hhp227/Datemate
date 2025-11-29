package com.hhp227.datemate.data.datasource

import android.app.Activity
import androidx.core.net.toUri
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hhp227.datemate.data.model.Profile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.TimeUnit

class UserRemoteDataSource private constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val userStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }

        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    fun signIn(email: String, password: String): Flow<FirebaseUser> = flow {
        val result = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()
        val user = result.user ?: throw Exception("로그인 실패")
        emit(user)
    }

    fun signOut(): Flow<Boolean> = flow {
        firebaseAuth.signOut()
        emit(true)
    }

    fun signUp(email: String, password: String): Flow<FirebaseUser> = flow {
        val result = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
        val user = result.user ?: throw Exception("회원가입 실패")
        emit(user)
    }

    fun sendPasswordResetEmail(email: String): Flow<Boolean> = flow {
        firebaseAuth.sendPasswordResetEmail(email).await()
        emit(true)
    }

    fun fetchUserProfile(userId: String): Flow<Profile?> = flow {
        val profileDocument = firestore.collection("profiles").document(userId).get().await()

        if (profileDocument.exists()) {
            val profile = profileDocument.toObject(Profile::class.java)?.apply {
                uid = profileDocument.id
            }

            emit(profile)
        } else {
            emit(null)
        }
    }

    fun createUserProfile(userId: String, email: String?): Flow<Boolean> = flow {
        val userDocument = firestore.collection("users").document(userId)
        val user = mapOf(
            "email" to email,
            "phoneNumber" to "",
            "createdAt" to FieldValue.serverTimestamp(),
            "lastLogin" to FieldValue.serverTimestamp(),
            "status" to "active"
        )

        firestore.runBatch { it.set(userDocument, user, SetOptions.merge()) }.await()
        emit(true)
    }

    fun updateUserProfile(
        userId: String,
        name: String,
        gender: String,
        birthdayMillis: Long,
        bio: String,
        job: String,
        profileImageUrls: List<String>?
    ): Flow<String> = userStateFlow.filterNotNull().map { user ->
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .setPhotoUri(profileImageUrls?.firstOrNull()?.toUri())
            .build()
        val userDocument = firestore.collection("profiles").document(userId)
        val userData = mapOf(
            "name" to name,
            "gender" to gender,
            "birthday" to Timestamp(Date(birthdayMillis)),
            "bio" to bio,
            "job" to job,
            "photos" to (profileImageUrls ?: emptyList()),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        user.updateProfile(profileUpdates).await()
        userDocument.set(userData, SetOptions.merge()).await()
        return@map userId
    }

    fun sendOtp(phoneNumber: String, activityProvider: () -> Activity): Flow<String> = callbackFlow {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                close(Exception("자동 인증 완료: ${credential.provider}"))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                close(e)
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                trySend(verificationId)
            }
        }
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activityProvider())
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        awaitClose { }
    }

    fun verifyOtp(verificationId: String, code: String): Flow<Unit> = userStateFlow.map { user ->
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        if (user == null) {
            Exception("현재 로그인된 사용자가 없습니다. 먼저 로그인해주세요.")
        } else {
            user.linkWithCredential(credential).await()
        }
    }

    companion object {
        @Volatile
        private var instance: UserRemoteDataSource? = null
        fun getInstance(
            auth: FirebaseAuth,
            firestore: FirebaseFirestore
        ) =
            instance ?: synchronized(this) {
                instance ?: UserRemoteDataSource(auth, firestore).also { instance = it }
            }
    }
}