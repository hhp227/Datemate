package com.hhp227.datemate;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hhp227.datemate.dto.User;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private EditText mEmailField, mPasswordField;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mProgressBar = findViewById(R.id.progress_bar);
        Button signInButton = findViewById(R.id.button_sign_in);
        Button signUpButton = findViewById(R.id.button_sign_up);

        signInButton.setOnClickListener(v -> {
            Log.d(TAG, "signIn");
            if (!validateForm())
                return;

            showProgressBar();
            String email = mEmailField.getText().toString();
            String password = mPasswordField.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                hideProgressBar();

                if (task.isSuccessful())
                    onAuthSuccess(task.getResult().getUser());
                else
                    Toast.makeText(SignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            });
        });
        signUpButton.setOnClickListener(v -> {
            Log.d(TAG, "signUp");
            if (!validateForm())
                return;

            showProgressBar();
            String email = mEmailField.getText().toString();
            String password = mPasswordField.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                hideProgressBar();

                if (task.isSuccessful())
                    onAuthSuccess(task.getResult().getUser());
                else
                    Toast.makeText(SignInActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            String username = usernameFromEmail(user.getEmail());

            writeNewUser(user.getUid(), username, user.getEmail());

            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        writeNewUser(user.getUid(), username, user.getEmail());

        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@"))
            return email.split("@")[0];
        else
            return email;
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else
            mEmailField.setError(null);

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else
            mPasswordField.setError(null);

        return result;
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabaseReference.child("users").child(userId).setValue(user);
    }

    private void showProgressBar() {
        if (mProgressBar != null)
            mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        if (mProgressBar != null)
            mProgressBar.setVisibility(View.INVISIBLE);
    }
}
