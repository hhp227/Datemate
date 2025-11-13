package com.hhp227.datemate.legacy.data

import com.google.firebase.database.*

class TestRepository(
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference,
    private val postRef: DatabaseReference = rootRef.child("posts")
) {

}