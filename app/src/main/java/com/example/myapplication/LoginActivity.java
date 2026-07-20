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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signUpTextView);
        progressBar = findViewById(R.id.loginProgressBar);

        loginButton.setOnClickListener(view -> loginUser());

        signUpTextView.setOnClickListener(view -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    SignUpActivity.class
            );
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = getText(emailEditText);
        String password = getText(passwordEditText);

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        setLoading(true);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    setLoading(false);

                    if (task.isSuccessful()) {
                        FirebaseUser user =
                                firebaseAuth.getCurrentUser();

                        openMainActivity(user);
                    } else {
                        String message = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unable to log in";

                        Toast.makeText(
                                LoginActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void openMainActivity(FirebaseUser user) {
        if (user == null) {
            Toast.makeText(
                    this,
                    "Unable to retrieve user information",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String userName = user.getDisplayName();

        if (userName == null || userName.trim().isEmpty()) {
            userName = user.getEmail() != null
                    ? user.getEmail().split("@")[0]
                    : "User";
        }

        Intent intent = new Intent(
                LoginActivity.this,
                MainActivity.class
        );

        intent.putExtra(MainActivity.EXTRA_USER_NAME, userName);
        intent.putExtra(MainActivity.EXTRA_USER_EMAIL, user.getEmail());

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

        loginButton.setEnabled(!loading);
        signUpTextView.setEnabled(!loading);
    }
}