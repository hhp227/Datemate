package com.hhp227.datemate.data

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TestRepository(
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference,
    private val postRef: DatabaseReference = rootRef.child("posts")
) {

}