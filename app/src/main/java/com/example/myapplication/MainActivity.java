package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA_USER_NAME = "user_name";
    public static final String EXTRA_USER_EMAIL = "user_email";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private String userName;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            openLoginActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        readUserInformation(currentUser);
        configureToolbar();
        configureNavigationDrawer();
        configureNavigationHeader();

        if (savedInstanceState == null) {
            replaceFragment(
                    HomeFragment.newInstance(userName),
                    "Home"
            );

            navigationView.setCheckedItem(R.id.navHome);
        }
    }

    private void readUserInformation(FirebaseUser user) {
        userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        userEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);

        if (userName == null || userName.trim().isEmpty()) {
            userName = user.getDisplayName();
        }

        if (userName == null || userName.trim().isEmpty()) {
            userName = user.getEmail() != null
                    ? user.getEmail().split("@")[0]
                    : "User";
        }

        if (userEmail == null || userEmail.trim().isEmpty()) {
            userEmail = user.getEmail();
        }

        if (userEmail == null) {
            userEmail = "";
        }
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
    }

    private void configureNavigationDrawer() {
        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        toolbar,
                        R.string.open_navigation_drawer,
                        R.string.close_navigation_drawer
                );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void configureNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);

        TextView userNameTextView =
                headerView.findViewById(R.id.headerUserNameTextView);

        TextView userEmailTextView =
                headerView.findViewById(R.id.headerEmailTextView);

        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);
    }

    @Override
    public boolean onNavigationItemSelected(
            @NonNull MenuItem item
    ) {
        int itemId = item.getItemId();

        if (itemId == R.id.navHome) {
            replaceFragment(
                    HomeFragment.newInstance(userName),
                    "Home"
            );
        } else if (itemId == R.id.navItems) {
            replaceFragment(
                    new ItemsFragment(),
                    "Products"
            );
        } else if (itemId == R.id.navAccount) {
            replaceFragment(
                    AccountFragment.newInstance(
                            userName,
                            userEmail
                    ),
                    "My Account"
            );
        } else if (itemId == R.id.navLanguage) {
            replaceFragment(
                    new LanguageFragment(),
                    "Language"
            );
        } else if (itemId == R.id.navLogout) {
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(
            Fragment fragment,
            String title
    ) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        toolbar.setTitle(title);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        openLoginActivity();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(
                MainActivity.this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null
                && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}