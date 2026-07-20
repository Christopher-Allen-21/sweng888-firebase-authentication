package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;

    private Button createAccountButton;
    private TextView loginTextView;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.signUpNameEditText);
        emailEditText = findViewById(R.id.signUpEmailEditText);
        passwordEditText = findViewById(R.id.signUpPasswordEditText);
        confirmPasswordEditText =
                findViewById(R.id.confirmPasswordEditText);

        createAccountButton =
                findViewById(R.id.createAccountButton);
        loginTextView = findViewById(R.id.loginTextView);
        progressBar = findViewById(R.id.signUpProgressBar);

        createAccountButton.setOnClickListener(
                view -> createAccount()
        );

        loginTextView.setOnClickListener(view -> finish());
    }

    private void createAccount() {
        String name = getText(nameEditText);
        String email = getText(emailEditText);
        String password = getText(passwordEditText);
        String confirmPassword =
                getText(confirmPasswordEditText);

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError(
                    "Password must contain at least 6 characters"
            );
            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError(
                    "Passwords do not match"
            );
            confirmPasswordEditText.requestFocus();
            return;
        }

        setLoading(true);

        firebaseAuth.createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        setLoading(false);

                        String message =
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Unable to create account";

                        Toast.makeText(
                                SignUpActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    updateFirebaseProfile(name, email);
                });
    }

    private void updateFirebaseProfile(
            String name,
            String email
    ) {
        if (firebaseAuth.getCurrentUser() == null) {
            setLoading(false);
            return;
        }

        UserProfileChangeRequest request =
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

        firebaseAuth.getCurrentUser()
                .updateProfile(request)
                .addOnCompleteListener(profileTask ->
                        saveUserToFirestore(name, email)
                );
    }

    private void saveUserToFirestore(
            String name,
            String email
    ) {
        if (firebaseAuth.getCurrentUser() == null) {
            setLoading(false);
            return;
        }

        String uid = firebaseAuth.getCurrentUser().getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("createdAt", System.currentTimeMillis());

        firestore.collection("users")
                .document(uid)
                .set(userData)
                .addOnSuccessListener(unused -> {
                    setLoading(false);

                    Toast.makeText(
                            this,
                            "Account created successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            SignUpActivity.this,
                            MainActivity.class
                    );

                    intent.putExtra(
                            MainActivity.EXTRA_USER_NAME,
                            name
                    );

                    intent.putExtra(
                            MainActivity.EXTRA_USER_EMAIL,
                            email
                    );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(exception -> {
                    setLoading(false);

                    Toast.makeText(
                            this,
                            "Account created, but user profile "
                                    + "could not be saved: "
                                    + exception.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                    openMainActivity(name, email);
                });
    }

    private void openMainActivity(
            String name,
            String email
    ) {
        Intent intent = new Intent(
                SignUpActivity.this,
                MainActivity.class
        );

        intent.putExtra(MainActivity.EXTRA_USER_NAME, name);
        intent.putExtra(MainActivity.EXTRA_USER_EMAIL, email);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() == null
                ? ""
                : editText.getText().toString().trim();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(
                loading ? View.VISIBLE : View.GONE
        );

        createAccountButton.setEnabled(!loading);
        loginTextView.setEnabled(!loading);
    }
}