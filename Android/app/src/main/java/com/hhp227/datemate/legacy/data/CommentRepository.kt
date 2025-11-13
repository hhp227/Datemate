package com.hhp227.datemate.legacy.data

import com.google.firebase.database.*
import com.hhp227.datemate.legacy.model.Comment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

class CommentRepository {
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val commentRef: DatabaseReference = rootRef.child("post-comments")

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

    fun addComment(key: String, text: String) {

    }
}