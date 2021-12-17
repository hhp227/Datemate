package com.hhp227.datemate.data

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hhp227.datemate.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class LoungeRepository(
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference,
    private val postRef: DatabaseReference = rootRef.child("posts")
) {
    fun getPosts(): Flow<List<Post>> {
        val mutableStateFlow = MutableStateFlow<List<Post>>(emptyList())

        postRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.children?.map { snapshot ->
                    snapshot.getValue(Post::class.java)!!
                }?.let {
                    mutableStateFlow.value = it
                }
            }
        }
        return mutableStateFlow
    }

    fun test() = "헬로우"
}