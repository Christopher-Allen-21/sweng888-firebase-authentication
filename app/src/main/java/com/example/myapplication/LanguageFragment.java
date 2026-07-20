package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LanguageFragment extends Fragment {

    private static final String PREFERENCES_NAME =
            "application_preferences";

    private static final String LANGUAGE_KEY =
            "selected_language";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.fragment_language,
                container,
                false
        );

        RadioGroup languageRadioGroup =
                view.findViewById(R.id.languageRadioGroup);

        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        PREFERENCES_NAME,
                        Context.MODE_PRIVATE
                );

        String selectedLanguage =
                preferences.getString(
                        LANGUAGE_KEY,
                        "English"
                );

        if ("Spanish".equals(selectedLanguage)) {
            languageRadioGroup.check(R.id.spanishRadioButton);
        } else if ("French".equals(selectedLanguage)) {
            languageRadioGroup.check(R.id.frenchRadioButton);
        } else {
            languageRadioGroup.check(R.id.englishRadioButton);
        }

        languageRadioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    String language;

                    if (checkedId == R.id.spanishRadioButton) {
                        language = "Spanish";
                    } else if (checkedId
                            == R.id.frenchRadioButton) {
                        language = "French";
                    } else {
                        language = "English";
                    }

                    preferences.edit()
                            .putString(LANGUAGE_KEY, language)
                            .apply();

                    Toast.makeText(
                            requireContext(),
                            language + " selected",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        return view;
    }
}