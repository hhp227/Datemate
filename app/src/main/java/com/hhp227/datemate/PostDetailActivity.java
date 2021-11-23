package com.hhp227.datemate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.hhp227.datemate.adapter.CommentAdapter;
import com.hhp227.datemate.dto.Comment;
import com.hhp227.datemate.dto.Post;
import com.hhp227.datemate.dto.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostDetailActivity extends AppCompatActivity {
    public static final String EXTRA_POST_KEY = "post_key";
    private static final String TAG = "PostDetailActivity";
    private DatabaseReference mPostReference, mCommentsReference;
    private CommentAdapter mAdapter;
    private TextView mAuthorView, mTitleView, mBodyView;
    private EditText mCommentField;
    private MaterialButton mCommentButton;
    private ListView mComentListView;
    private ValueEventListener mPostListener;
    private List<String> mCommentIds;
    private List<Comment> mComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        String postKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (postKey == null)
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");

        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts").child(postKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference().child("post-comments").child(postKey);

        mCommentIds = new ArrayList<>();
        mComments = new ArrayList<>();

        mAuthorView = findViewById(R.id.post_author);
        mTitleView = findViewById(R.id.post_title);
        mBodyView = findViewById(R.id.post_body);
        mCommentField = findViewById(R.id.field_comment_text);
        mCommentButton = findViewById(R.id.button_post_comment);
        mComentListView = findViewById(R.id.list_post_comments);

        mPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                mAuthorView.setText(post.author);
                mTitleView.setText(post.title);
                mBodyView.setText(post.body);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(PostDetailActivity.this, "Failed to load post.", Toast.LENGTH_SHORT).show();
            }
        };
        mCommentButton.setOnClickListener(v -> {
            String uid = getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    String authorName = user.username;

                    String commentText = mCommentField.getText().toString();
                    Comment comment = new Comment(uid, authorName, commentText);

                    mCommentsReference.push().setValue(comment);

                    mCommentField.setText(null);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPostReference.addValueEventListener(mPostListener);
        mAdapter = new CommentAdapter(this, mCommentIds, mComments);
        mCommentsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                Comment comment = dataSnapshot.getValue(Comment.class);

                mCommentIds.add(dataSnapshot.getKey());
                mComments.add(comment);
                mAdapter.notifyDataSetChanged();
                // Update RecyclerView
                //notifyItemInserted(mComments.size() - 1);
                Toast.makeText(getApplicationContext(), mCommentIds.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                int commentIndex = mCommentIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    mComments.set(commentIndex, newComment);

                    mAdapter.notifyDataSetChanged();
                    // Update the RecyclerView
                    //notifyItemChanged(commentIndex);
                } else
                    Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                String commentKey = dataSnapshot.getKey();

                int commentIndex = mCommentIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    mCommentIds.remove(commentIndex);
                    mComments.remove(commentIndex);

                    mAdapter.notifyDataSetChanged();
                    // Update the RecyclerView
                    //notifyItemRemoved(commentIndex);
                } else
                    Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to load comments.", Toast.LENGTH_SHORT).show();
            }
        });
        mComentListView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPostListener != null) 
            mPostReference.removeEventListener(mPostListener);
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
