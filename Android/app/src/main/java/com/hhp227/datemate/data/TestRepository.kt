package com.hhp227.datemate.data

import com.google.firebase.database.*
import com.hhp227.datemate.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TestRepository(
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference,
    private val postRef: DatabaseReference = rootRef.child("posts")
) {

}