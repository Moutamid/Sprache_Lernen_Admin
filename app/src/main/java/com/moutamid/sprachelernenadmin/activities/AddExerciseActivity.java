package com.moutamid.sprachelernenadmin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityAddExerciseBinding;
import com.moutamid.sprachelernenadmin.models.ExerciseModel;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import java.util.ArrayList;
import java.util.UUID;

public class AddExerciseActivity extends AppCompatActivity {
    ActivityAddExerciseBinding binding;
    int optionsCount = 1;
    ArrayList<String> options;
    String level = "";
    String exercise = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.title.setText("Add Exercise");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        options = new ArrayList<>();

        level = getIntent().getStringExtra(Constants.LEVEL);
        exercise = getIntent().getStringExtra(Constants.exercise);

        addOption();
        addOption();

        binding.addOption.setOnClickListener(v -> addOption());

        binding.next.setOnClickListener(v -> {
            retrieveDataForOptions();
            boolean isFTBChecked = binding.isFTB.isChecked();
            boolean isMultipleChecked = binding.isMultiple.isChecked();
            boolean isReorderChecked = binding.isReorder.isChecked();
            ExerciseModel model = new ExerciseModel(UUID.randomUUID().toString(), level,
                    binding.question.getEditText().getText().toString(),
                    exercise,
                    options,
                    binding.answer.getEditText().getText().toString(),
                    isMultipleChecked, isFTBChecked, isReorderChecked
            );
            Constants.showDialog();

            Constants.databaseReference().child(Constants.getLang()).child(Constants.EXERCISE).child(level).child(model.getID()).setValue(model)
                    .addOnSuccessListener(unused -> {
                        Constants.dismissDialog();
                        Toast.makeText(AddExerciseActivity.this, "Exercise Added Successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(AddExerciseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private void retrieveDataForOptions() {
        options.clear();
        for (int i = 0; i < binding.optionsLayout.getChildCount(); i++) {
            View view = binding.optionsLayout.getChildAt(i);
            if (view instanceof RelativeLayout) {
                RelativeLayout textInputLayout = (RelativeLayout) view;
                TextInputLayout customEditText = textInputLayout.findViewById(R.id.addColumn);

                String enteredText = customEditText.getEditText().getText().toString();
                if (!enteredText.isEmpty())
                    options.add(enteredText);
            }
        }
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