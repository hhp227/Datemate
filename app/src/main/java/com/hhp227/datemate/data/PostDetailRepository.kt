package com.hhp227.datemate.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hhp227.datemate.model.Comment
import com.hhp227.datemate.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class PostDetailRepository {
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val postRef: DatabaseReference = rootRef.child("posts")
    
    private val userPostRef: DatabaseReference = rootRef.child("user-posts")

    private val commentRef: DatabaseReference = rootRef.child("post-comments")

    fun getPost(key: String) = callbackFlow {
        postRef.child(key)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySendBlocking(task.result.key?.let { task.result.getValue(Post::class.java)?.apply { this.key = it } } ?: Post())
                }
            }.addOnFailureListener {
                trySendBlocking(Post())
            }
        awaitClose { close() }
    }

    fun getComments(key: String) = callbackFlow<List<Comment>> {
        commentRef.child(key).also { commentChildRef ->
            commentChildRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    runCatching { trySendBlocking(snapshot.children.mapNotNull { it.getValue(Comment::class.java) }) }
                    commentChildRef.removeEventListener(this)
                }

                override fun onCancelled(error: DatabaseError) {
                    runCatching { trySendBlocking(emptyList()) }
                    commentChildRef.removeEventListener(this)
                }
            })
        }
        awaitClose { channel.close() }
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