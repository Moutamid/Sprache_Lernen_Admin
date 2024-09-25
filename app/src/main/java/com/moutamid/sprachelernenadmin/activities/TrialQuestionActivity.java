 package com.moutamid.sprachelernenadmin.activities;
import com.moutamid.sprachelernenadmin.Stash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityTrialQuestionBinding;
import com.moutamid.sprachelernenadmin.models.Exercise;
import com.moutamid.sprachelernenadmin.models.ExerciseModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class TrialQuestionActivity extends AppCompatActivity {
    ActivityTrialQuestionBinding binding;
    int optionsCount = 1;
    ArrayList<String> options;
    Uri audio;
    private static final int PICK_AUDIO_REQUEST = 1;
    String audioPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrialQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("New Trial Question");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        options = new ArrayList<>();

        addOption();
        addOption();

        binding.addOption.setOnClickListener(v -> addOption());

        binding.uploadAdio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST);
        });

        binding.next.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                uploadAudio();
            }
        });
    }

    private void uploadAudio() {
        Constants.storageReference().child("audio").child(Constants.getFormattedDate(new Date().getTime())).putFile(audio)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        audioPath = uri.toString();
                        uploadData();
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData() {
        retrieveDataForOptions();
        boolean isFTBChecked = binding.isFTB.isChecked();
        boolean isMultipleChecked = binding.isMultiple.isChecked();
        boolean isReorderChecked = binding.isReorder.isChecked();
        ExerciseModel model = new ExerciseModel(UUID.randomUUID().toString(),"", "Start",
                binding.question.getEditText().getText().toString(),
                "Trial Questions",
                options,
                binding.answer.getEditText().getText().toString(),
                isMultipleChecked, isFTBChecked, isReorderChecked,
                binding.explain.getEditText().getText().toString(), audioPath, 1, Integer.parseInt(binding.questionCount.getEditText().getText().toString())
        );
        Constants.showDialog();
        Constants.databaseReference().child(Constants.TRIAL_QUESTIONS).child(model.getID()).setValue(model)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(TrialQuestionActivity.this, "Exercise Added Successfully", Toast.LENGTH_SHORT).show();
                    clearView();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(TrialQuestionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearView() {
        audioPath = "";
        optionsCount = 1;
        audio = null;
        binding.uploadAdio.setText("Upload Audio");
        binding.question.getEditText().setText("");
        binding.answer.getEditText().setText("");
        binding.explain.getEditText().setText("");
        int count = Integer.parseInt(binding.questionCount.getEditText().getText().toString()) + 1;
        binding.questionCount.getEditText().setText(String.valueOf(count));

        binding.isMultiple.setChecked(false);
        binding.isReorder.setChecked(false);
        binding.isFTB.setChecked(false);
        options.clear();
        binding.optionsLayout.removeAllViews();
        optionsCount = 1;
        addOption();
        addOption();
    }

    private boolean valid() {
        retrieveDataForOptions();
        if (!(options.size()>1)){
            Toast.makeText(this, "Options are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.isReorder.isChecked() && binding.isFTB.isChecked()) {
            Toast.makeText(this, "Please choose either Reorder or Fill in the Blank, not both.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.isReorder.isChecked() && binding.isMultiple.isChecked()) {
            Toast.makeText(this, "Please choose either Reorder or Multiple Choice, not both.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.isFTB.isChecked() && binding.isMultiple.isChecked()) {
            Toast.makeText(this, "Please choose either Fill in the Blank or Multiple Choice, not both.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.isFTB.isChecked() && binding.isMultiple.isChecked() && binding.isReorder.isChecked()) {
            Toast.makeText(this, "Please choose only one question type.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.explain.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Explanation is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.question.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Question is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.questionCount.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Question Count is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.answer.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Right Answer is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                audio = data.getData();
                binding.uploadAdio.setText("Audio File: " + Constants.getFileName(this,audio));
            }
        }
    }

}