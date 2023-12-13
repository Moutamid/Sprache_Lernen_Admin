package com.moutamid.sprachelernenadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.moutamid.sprachelernenadmin.bottomsheet.ContentTopicList;
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
            ContentTopicList bottomSheetFragment = new ContentTopicList(true);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
        binding.exercise.setOnClickListener(v -> {
            ContentTopicList bottomSheetFragment = new ContentTopicList(false);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

    }
}