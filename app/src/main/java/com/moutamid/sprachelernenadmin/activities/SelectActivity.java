package com.moutamid.sprachelernenadmin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.bottomsheet.ContentTopicList;
import com.moutamid.sprachelernenadmin.bottomsheet.LevelSelection;
import com.moutamid.sprachelernenadmin.databinding.ActivitySelectBinding;

public class SelectActivity extends AppCompatActivity {
    ActivitySelectBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText(getString(R.string.app));
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        binding.topic.setOnClickListener(v -> startActivity(new Intent(this, AddTopicsActivity.class)));
        binding.content.setOnClickListener(v -> {
            ContentTopicList bottomSheetFragment = new ContentTopicList();
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
        binding.exercise.setOnClickListener(v -> {
            LevelSelection bottomSheetFragment = new LevelSelection();
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        binding.viewContent.setOnClickListener(v -> startActivity(new Intent(this, ViewContentActivity.class)));
        binding.viewExercise.setOnClickListener(v -> startActivity(new Intent(this, ViewExerciseActivity.class)));

    }
}