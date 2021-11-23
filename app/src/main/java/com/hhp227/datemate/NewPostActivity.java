package com.hhp227.datemate;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.hhp227.datemate.dto.Post;
import com.hhp227.datemate.dto.User;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {
    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    private DatabaseReference mDatabaseReference;
    private EditText mTitleField, mBodyField;
    private FloatingActionButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mTitleField = findViewById(R.id.field_title);
        mBodyField = findViewById(R.id.field_body);
        mSubmitButton = findViewById(R.id.fab_submit_post);

        mSubmitButton.setOnClickListener(v -> submitPost());
    }

    private void submitPost() {
        String title = mTitleField.getText().toString();
        String body = mBodyField.getText().toString();

        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        String userId = getUid();
        mDatabaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    Log.e(TAG, "User " + userId + " is unexpectedly null");
                    Toast.makeText(NewPostActivity.this, "Error: could not fetch user.", Toast.LENGTH_SHORT).show();
                } else {
                    String key = mDatabaseReference.child("posts").push().getKey();
                    Post post = new Post(userId, user.username, title, body);
                    Map<String, Object> postValues = post.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/posts/" + key, postValues);
                    childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

                    mDatabaseReference.updateChildren(childUpdates);
                }

                setEditingEnabled(true);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled)
            mSubmitButton.show();
        else
            mSubmitButton.hide();
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
