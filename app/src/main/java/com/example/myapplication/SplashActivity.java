package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser =
                    FirebaseAuth.getInstance().getCurrentUser();

            Intent intent;

            if (currentUser != null) {
                intent = new Intent(
                        SplashActivity.this,
                        MainActivity.class
                );

                intent.putExtra(
                        MainActivity.EXTRA_USER_NAME,
                        getUserName(currentUser)
                );

                intent.putExtra(
                        MainActivity.EXTRA_USER_EMAIL,
                        currentUser.getEmail()
                );
            } else {
                intent = new Intent(
                        SplashActivity.this,
                        LoginActivity.class
                );
            }

            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }

    private String getUserName(FirebaseUser user) {
        if (user.getDisplayName() != null
                && !user.getDisplayName().trim().isEmpty()) {
            return user.getDisplayName();
        }

        if (user.getEmail() != null) {
            return user.getEmail().split("@")[0];
        }

        return "User";
    }
}