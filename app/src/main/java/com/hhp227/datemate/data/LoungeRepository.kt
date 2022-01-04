package com.hhp227.datemate.data

import com.google.firebase.database.*
import com.hhp227.datemate.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class LoungeRepository(
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference,
    private val postRef: DatabaseReference = rootRef.child("posts")
) {
    fun getPosts(): Flow<List<Post>> = callbackFlow {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                runCatching {
                    trySendBlocking(snapshot.children.mapNotNull { dataSnapshot ->
                        dataSnapshot.key?.let { dataSnapshot.getValue(Post::class.java)?.apply { key = it } } ?: Post()
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                runCatching { trySendBlocking(emptyList()) }
            }
        }

        postRef.addListenerForSingleValueEvent(postListener)
        awaitClose {
            postRef.removeEventListener(postListener)
            close()
        }
    }

    fun test() = "헬로우"
}