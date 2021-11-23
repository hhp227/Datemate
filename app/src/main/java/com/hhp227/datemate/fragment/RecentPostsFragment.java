package com.hhp227.datemate.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecentPostsFragment extends PostListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query recentPostsQuery = databaseReference.child("posts").limitToFirst(100);
        return recentPostsQuery;
    }
}
