 package com.moutamid.sprachelernenadmin.activities;
import com.moutamid.sprachelernenadmin.Stash;

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

        binding.exercise.setOnClickListener(v -> {
            LevelSelection bottomSheetFragment = new LevelSelection(false);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        binding.newExercise.setOnClickListener(v -> {
            LevelSelection bottomSheetFragment = new LevelSelection(true);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        binding.topic.setOnClickListener(v -> startActivity(new Intent(this, AddTopicsActivity.class)));
        binding.writing.setOnClickListener(v -> startActivity(new Intent(this, WritingActivity.class)));
        binding.vocabualry.setOnClickListener(v -> startActivity(new Intent(this, VocabularyActivity.class)));
        binding.viewContent.setOnClickListener(v -> startActivity(new Intent(this, ViewContentActivity.class)));
        binding.viewExercise.setOnClickListener(v -> startActivity(new Intent(this, ViewExerciseActivity.class)));
        binding.trialQuestions.setOnClickListener(v -> startActivity(new Intent(this, TrialQuestionActivity.class)));
        binding.viewTrial.setOnClickListener(v -> startActivity(new Intent(this, ViewTrialActivity.class)));

        binding.payment.setOnClickListener(v -> startActivity(new Intent(this, PaymentActivity.class)));

    }
}