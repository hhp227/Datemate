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

    suspend fun signIn(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("로그인 실패")
    }

    fun signOut(): Boolean {
        firebaseAuth.signOut()
        return true
    }

    suspend fun signUp(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("회원가입 실패")
    }

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        firebaseAuth.sendPasswordResetEmail(email).await()
        return true
    }

    suspend fun fetchUserProfile(userId: String): Profile? {
        val snapshot = firestore.collection("profiles").document(userId).get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(Profile::class.java)?.apply { uid = snapshot.id }
        } else null
    }

    suspend fun createUserProfile(userId: String, email: String?): Boolean {
        val doc = firestore.collection("users").document(userId)
        val data = mapOf(
            "email" to email,
            "phoneNumber" to "",
            "createdAt" to FieldValue.serverTimestamp(),
            "lastLogin" to FieldValue.serverTimestamp(),
            "status" to "active"
        )

        firestore.runBatch { it.set(doc, data, SetOptions.merge()) }.await()
        return true
    }

    suspend fun updateUserProfile(name: String, photoUrl: String?): Boolean {
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .setPhotoUri(photoUrl?.toUri())
            .build()

        firebaseAuth.currentUser?.updateProfile(request)?.await() ?: return false
        return true
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
                trySend(verificationId).isSuccess
            }
        }
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activityProvider())
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        awaitClose()
    }

    suspend fun verifyOtp(verificationId: String, code: String) {
        val current = firebaseAuth.currentUser ?: throw Exception("로그인된 사용자가 없습니다.")
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        current.linkWithCredential(credential).await()
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