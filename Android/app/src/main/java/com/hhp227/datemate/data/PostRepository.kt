package com.hhp227.datemate.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hhp227.datemate.data.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class PostRepository {
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val postRef: DatabaseReference = rootRef.child("posts")

    private val userPostRef: DatabaseReference = rootRef.child("user-posts")

    fun getPosts(): Flow<List<Post>> = callbackFlow {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { dataSnapshot ->
                    dataSnapshot.key?.let { dataSnapshot.getValue(Post::class.java)?.apply { key = it } } ?: Post()
                })
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }

        postRef.addListenerForSingleValueEvent(postListener)
        awaitClose {
            postRef.removeEventListener(postListener)
            close()
        }
    }

    fun getPost(key: String) = callbackFlow {
        postRef.child(key)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySendBlocking(task.result.key?.let {
                        task.result.getValue(Post::class.java)?.apply { this.key = it }
                    } ?: Post())
                }
            }.addOnFailureListener {
                trySendBlocking(Post())
            }
        awaitClose { close() }
    }

    fun addPost(title: String, content: String) {

    }

    fun removePost(key: String) {

    }

    fun getUserPostKeys(): Flow<List<String>> {
        val mutableStateFlow = MutableStateFlow<List<String>>(emptyList())

        FirebaseAuth.getInstance().currentUser?.also { user ->
            userPostRef.child(user.uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mutableStateFlow.value = task.result.children.mapNotNull { it.key }
                }
            }
        }
        return mutableStateFlow
    }
}