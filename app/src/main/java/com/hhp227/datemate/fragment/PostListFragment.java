package com.hhp227.datemate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.hhp227.datemate.PostDetailActivity;
import com.hhp227.datemate.R;
import com.hhp227.datemate.dto.Post;

public abstract class PostListFragment extends Fragment {
    private static final String TAG = "PostListFragment";
    private DatabaseReference mDatabaseReference;
    private FirebaseListAdapter mAdapter;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mListView = rootView.findViewById(R.id.messages_list);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Query postsQuery = getQuery(mDatabaseReference);

        FirebaseListOptions<Post> options = new FirebaseListOptions.Builder<Post>().setLayout(R.layout.item_post).setQuery(postsQuery, Post.class).build();
        mAdapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View view, Object model, int position) {
                TextView titleView = view.findViewById(R.id.post_title);
                TextView authorView = view.findViewById(R.id.post_author);
                ImageView starView = view.findViewById(R.id.star);
                TextView numStarsView = view.findViewById(R.id.post_num_stars);
                TextView bodyView = view.findViewById(R.id.post_body);

                DatabaseReference postRef = getRef(position);

                String postKey = postRef.getKey();
                view.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                    intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                    startActivity(intent);
                });
                starView.setImageResource(((Post) model).stars.containsKey(getUid()) ? R.drawable.ic_toggle_star_24 : R.drawable.ic_toggle_star_outline_24);
                titleView.setText(((Post) model).title);
                authorView.setText(((Post) model).author);
                numStarsView.setText(String.valueOf(((Post) model).starCount));
                bodyView.setText(((Post) model).body);

                starView.setOnClickListener(v -> {
                    DatabaseReference globalPostRef = mDatabaseReference.child("posts").child(postRef.getKey());
                    DatabaseReference userPostRef = mDatabaseReference.child("user-posts").child(((Post) model).uid).child(postRef.getKey());

                    onStarClicked(globalPostRef);
                    onStarClicked(userPostRef);
                });
            }
        };
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null)
            mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null)
            mAdapter.stopListening();
    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null)
                    return Transaction.success(mutableData);

                if (p.stars.containsKey(getUid())) {
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}
