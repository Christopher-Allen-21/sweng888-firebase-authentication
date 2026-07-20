package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

public class HomeFragment extends Fragment {

    private static final String ARG_USER_NAME = "user_name";

    public static HomeFragment newInstance(String userName) {
        HomeFragment fragment = new HomeFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_USER_NAME, userName);

        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        TextView welcomeTextView =
                view.findViewById(R.id.welcomeTextView);

        String userName = "User";

        if (getArguments() != null) {
            userName = getArguments().getString(
                    ARG_USER_NAME,
                    "User"
            );
        }

        welcomeTextView.setText(
                "Welcome, " + userName + "!"
        );

        return view;
    }
}