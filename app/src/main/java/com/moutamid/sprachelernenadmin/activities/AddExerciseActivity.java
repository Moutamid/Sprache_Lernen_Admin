 package com.moutamid.sprachelernenadmin.activities;
import com.moutamid.sprachelernenadmin.Stash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
 
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityAddExerciseBinding;
import com.moutamid.sprachelernenadmin.models.Exercise;
import com.moutamid.sprachelernenadmin.models.ExerciseModel;
import com.moutamid.sprachelernenadmin.Stash;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class AddExerciseActivity extends AppCompatActivity {
    ActivityAddExerciseBinding binding;
    int optionsCount = 1;
    ArrayList<String> options;
    String level = "";
    String exercise = "";
    int exerciseCount = 0;
    Uri audio;
    Exercise exerciseModel;
    private static final int PICK_AUDIO_REQUEST = 1;
    String audioPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.title.setText("Add Exercise");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        options = new ArrayList<>();

        exerciseModel = (Exercise) Stash.getObject(Constants.Exercise, Exercise.class);
        level = exerciseModel.getLevel();
        exercise = exerciseModel.getName();
        exerciseCount = exerciseModel.getExerciseCount();

        addOption();
        addOption();

        binding.addOption.setOnClickListener(v -> addOption());

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < exerciseCount; i++) {
            list.add(""+(i+1));
        }
        ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list);
        binding.exerciseList.setAdapter(exerciseAdapter);

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
        ExerciseModel model = new ExerciseModel(UUID.randomUUID().toString(), exerciseModel.getID(), level,
                binding.question.getEditText().getText().toString(),
                exercise,
                options,
                binding.answer.getEditText().getText().toString(),
                isMultipleChecked, isFTBChecked, isReorderChecked,
                binding.explain.getEditText().getText().toString(), audioPath, Integer.parseInt(binding.exerciseNumber.getEditText().getText().toString()),
                Integer.parseInt(binding.questionCount.getEditText().getText().toString())
        );
        Constants.databaseReference().child(Constants.getLang()).child(Constants.EXERCISE).child(level).child(exerciseModel.getID()).child(String.valueOf(model.getExerciseCount())).child(model.getID()).setValue(model)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(AddExerciseActivity.this, "Exercise Added Successfully", Toast.LENGTH_SHORT).show();
                   updateView();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(AddExerciseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateView() {
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
        if (binding.exerciseNumber.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Exercise Number is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (audio == null){
            Toast.makeText(this, "Add Audio File", Toast.LENGTH_SHORT).show();
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