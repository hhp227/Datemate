package com.hhp227.datemate.data

import android.util.Log
import com.google.firebase.database.*
import com.hhp227.datemate.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PostDetailRepository(
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference,
    private val postRef: DatabaseReference = rootRef.child("posts"),
    private val userPostRef: DatabaseReference = rootRef.child("user-posts"),
    private val commentRef: DatabaseReference = rootRef.child("post-comments")
) {
    fun getPost(key: String): Flow<Post> {
        val mutableFlow = MutableStateFlow(Post())

        postRef.child(key).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                mutableFlow.value = task.result.key?.let { task.result.getValue(Post::class.java)?.apply { this.key = it } } ?: Post()
            }
        }
        return mutableFlow
    }
}