package com.moutamid.sprachelernenadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.databinding.ActivityAddExerciseBinding;

public class AddExerciseActivity extends AppCompatActivity {
    ActivityAddExerciseBinding binding;
    int optionsCount = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.title.setText("Add Exercise");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        addOption();
        addOption();

        binding.addOption.setOnClickListener(v -> addOption());

    }

    private void addOption() {
        LayoutInflater inflater = getLayoutInflater();
        View customEditTextLayout = inflater.inflate(R.layout.option_edittext, null);
        TextInputLayout customEditText = customEditTextLayout.findViewById(R.id.addColumn);
        customEditText.setHint("Option " + optionsCount);
        optionsCount++;
        binding.optionsLayout.addView(customEditTextLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }
}