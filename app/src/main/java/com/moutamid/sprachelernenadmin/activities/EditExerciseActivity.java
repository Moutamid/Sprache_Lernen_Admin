package com.moutamid.sprachelernenadmin.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityEditExerciseBinding;
import com.moutamid.sprachelernenadmin.models.ExerciseModel;

import java.util.ArrayList;
import java.util.Date;

public class EditExerciseActivity extends AppCompatActivity {
    ActivityEditExerciseBinding binding;
    int optionsCount = 1;
    ArrayList<String> options;
    Uri audio;
    private static final int PICK_AUDIO_REQUEST = 1;
    String audioPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.title.setText("Edit Exercise");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        options = new ArrayList<>();

        ExerciseModel model = (ExerciseModel) Stash.getObject(Constants.PASS_EXERCISE, ExerciseModel.class);
        setContent(model);

        binding.addOption.setOnClickListener(v -> addOption());

        binding.uploadAdio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST);
        });

        binding.next.setOnClickListener(v -> {
            if (valid()) {
                if (audio != null){
                    uploadAudio();
                } else {
                    UploadData(model.getVoiceover());
                }
            }
        });
    }

    private void UploadData(String voiceover) {
        ExerciseModel model = (ExerciseModel) Stash.getObject(Constants.PASS_EXERCISE, ExerciseModel.class);
        retrieveDataForOptions();
        boolean isFTBChecked = binding.isFTB.isChecked();
        boolean isMultipleChecked = binding.isMultiple.isChecked();
        boolean isReorderChecked = binding.isReorder.isChecked();

        ExerciseModel exerciseModel = new ExerciseModel(model.getID(), model.getExerciseID(), model.getLevel(),
                binding.question.getEditText().getText().toString(),
                model.getExerciseName(),
                options,
                binding.answer.getEditText().getText().toString(),
                isMultipleChecked, isFTBChecked, isReorderChecked,
                binding.explain.getEditText().getText().toString(), voiceover, Integer.parseInt(binding.exerciseNumber.getEditText().getText().toString()), Integer.parseInt(binding.questionCount.getEditText().getText().toString())
        );
        Constants.showDialog();
        Constants.databaseReference().child(Constants.getLang()).child(Constants.EXERCISE).child(exerciseModel.getLevel()).child(exerciseModel.getExerciseID()).child(String.valueOf(exerciseModel.getExerciseCount())).child(exerciseModel.getID()).setValue(exerciseModel)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(EditExerciseActivity.this, "Exercise Updated Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(EditExerciseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadAudio() {
        Constants.storageReference().child("audio").child(Constants.getFormattedDate(new Date().getTime())).putFile(audio)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        UploadData(uri.toString());
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
            Toast.makeText(this, "Explanation are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.question.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Question are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.questionCount.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Question Count is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.answer.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Right Answer are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setContent(ExerciseModel model) {
        binding.question.getEditText().setText(model.getQuestion());
        binding.answer.getEditText().setText(model.getRightAnswer());
        binding.explain.getEditText().setText(model.getExplanation());
        binding.exerciseNumber.getEditText().setText(model.getExerciseCount()+"");
        binding.questionCount.getEditText().setText(model.getQuestionCount()+"");

        boolean isFillBlank = model.isFillBlank();
        boolean isOrder = model.isOrder();
        boolean isMCQs = model.isMCQs();

        binding.isMultiple.setChecked(isMCQs);
        binding.isReorder.setChecked(isOrder);
        binding.isFTB.setChecked(isFillBlank);

        binding.optionsLayout.removeAllViews();
        for (int i = 0; i < model.getOptions().size(); i++) {
            addOption();
        }
        for (int i = 0; i < binding.optionsLayout.getChildCount(); i++) {
            View view = binding.optionsLayout.getChildAt(i);
            if (view instanceof RelativeLayout) {
                RelativeLayout textInputLayout = (RelativeLayout) view;
                TextInputLayout customEditText = textInputLayout.findViewById(R.id.addColumn);
                customEditText.getEditText().setText(model.getOptions().get(i));
            }
        }
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