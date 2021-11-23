package com.hhp227.datemate.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-posts").child(getUid());
    }
}
