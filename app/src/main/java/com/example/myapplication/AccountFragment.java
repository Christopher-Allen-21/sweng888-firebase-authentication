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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_EMAIL = "email";

    public static AccountFragment newInstance(
            String name,
            String email
    ) {
        AccountFragment fragment = new AccountFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_NAME, name);
        arguments.putString(ARG_EMAIL, email);

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
                R.layout.fragment_account,
                container,
                false
        );

        TextView nameTextView =
                view.findViewById(R.id.accountNameTextView);

        TextView emailTextView =
                view.findViewById(R.id.accountEmailTextView);

        TextView uidTextView =
                view.findViewById(R.id.accountUidTextView);

        String name = "";
        String email = "";

        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME, "");
            email = getArguments().getString(ARG_EMAIL, "");
        }

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        nameTextView.setText(name);
        emailTextView.setText(email);

        uidTextView.setText(
                currentUser != null
                        ? currentUser.getUid()
                        : "Unavailable"
        );

        return view;
    }
}