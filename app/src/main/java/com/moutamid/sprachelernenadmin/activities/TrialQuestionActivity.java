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

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityTrialQuestionBinding;
import com.moutamid.sprachelernenadmin.models.ContentModel;
import com.moutamid.sprachelernenadmin.models.Exercise;
import com.moutamid.sprachelernenadmin.models.ExerciseModel;
import com.moutamid.sprachelernenadmin.models.TrialContent;

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

    ArrayList<String> optionsList;
    ArrayList<String> rows;
    String image = "";
    String imageHeading = "", subHeading = "";
    Uri img;
    private static final int PICK_AUDIO_REQUEST_CONTENT = 3;
    private static final int PICK_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrialQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("Add Trial Content");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        binding.isContent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.isExercise.setChecked(false);
                binding.otherLayout.setVisibility(View.VISIBLE);
                binding.exerciseLayout.setVisibility(View.GONE);
            } else {
                binding.otherLayout.setVisibility(View.GONE);
                binding.exerciseLayout.setVisibility(View.GONE);
                binding.showImage.setChecked(false);
            }
        });

        binding.isExercise.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.isContent.setChecked(false);
                binding.exerciseLayout.setVisibility(View.VISIBLE);
                binding.otherLayout.setVisibility(View.GONE);
            } else {
                binding.otherLayout.setVisibility(View.GONE);
                binding.exerciseLayout.setVisibility(View.GONE);
            }
        });

        binding.isContent.setChecked(true);

        options = new ArrayList<>();
        optionsList = new ArrayList<>();
        rows = new ArrayList<>();

        addOption();
        addOption();

        binding.addOption.setOnClickListener(v -> addOption());

        binding.uploadAdio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST);
        });

        binding.addExercise.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                uploadAudio();
            }
        });

        binding.showTable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int v = isChecked ? View.VISIBLE : View.GONE;
            binding.tableLayout.setVisibility(v);
            if (!isChecked) {
                binding.columnLayout.removeAllViews();
                addRow();
            }
        });
        binding.showImage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int v = isChecked ? View.VISIBLE : View.GONE;
            binding.imageLayout.setVisibility(v);
        });

        binding.showList.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int v = isChecked ? View.VISIBLE : View.GONE;
            binding.options.setVisibility(v);
            if (!isChecked) {
                binding.optionsLayout.removeAllViews();
                addOptionContent();
            }
        });

        binding.audio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST_CONTENT);
        });

        binding.image.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .galleryOnly()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start(PICK_IMAGE_REQUEST);
        });

        addOptionContent();
        addRow();

        binding.addOption.setOnClickListener(v -> addOptionContent());
        binding.addRow.setOnClickListener(v -> addRow());

        binding.AddContent.setOnClickListener(v -> {
            if (valid())
                if (audio == null){
                    if (binding.showImage.isChecked()) {
                        uploadImage();
                    } else {
                        uploadData();
                    }
                } else {
                    uploadAudio();
                }
        });


    }

    private void uploadImage() {
        Constants.storageReference().child("images").child(Constants.getFormattedDate(new Date().getTime())).putFile(img)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        image = uri.toString();
                        uploadData();
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void uploadAudio() {
        Constants.storageReference().child("audio").child(Constants.getFormattedDate(new Date().getTime())).putFile(audio)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        audioPath = uri.toString();
                        if (binding.showImage.isChecked()) {
                            uploadImage();
                        } else {
                            uploadData();
                        }
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData() {
        retrieveDataForOptions();
        boolean haveRows = binding.showTable.isChecked();
        boolean hasOptions = binding.showList.isChecked();

        if (haveRows) retrieveDataForRows();
        if (hasOptions) retrieveDataForOptionsList();

        if (binding.showImage.isChecked()) {
            imageHeading = binding.imaegHeading.getEditText().getText().toString();
            subHeading = binding.subHeading.getEditText().getText().toString();
        }

        ContentModel content = new ContentModel();
        ExerciseModel exercise = new ExerciseModel();

        if (binding.isContent.isChecked()) {
            content = new ContentModel(UUID.randomUUID().toString(), null,
                    binding.heading.getEditText().getText().toString(),
                    binding.note.getEditText().getText().toString(), image, audioPath, imageHeading, subHeading, hasOptions, haveRows, optionsList, rows,
                    Integer.parseInt(binding.count.getEditText().getText().toString()));
        } else {
            exercise = new ExerciseModel(UUID.randomUUID().toString(),"", "Start",
                    binding.question.getEditText().getText().toString(),
                    "Trial Questions",
                    options,
                    binding.answer.getEditText().getText().toString(),
                    false, false, false,
                    binding.explain.getEditText().getText().toString(), audioPath, 1, Integer.parseInt(binding.questionCount.getEditText().getText().toString())
            );
        }
        Constants.showDialog();
        String c = binding.count.getEditText().getText().toString().isEmpty() ? "0" : binding.count.getEditText().getText().toString();
        TrialContent trialContent = new TrialContent(
                UUID.randomUUID().toString(), Integer.parseInt(c),
                exercise, content, binding.isExercise.isChecked(), binding.isContent.isChecked()
        );

        Constants.databaseReference().child(Constants.TRIAL_QUESTIONS).child(trialContent.getUid()).setValue(trialContent)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(TrialQuestionActivity.this, "Content Added Successfully", Toast.LENGTH_SHORT).show();
                    getOnBackPressedDispatcher().onBackPressed();
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

        options.clear();
        binding.optionsLayout.removeAllViews();
        optionsCount = 1;
        addOption();
        addOption();
    }

    private boolean valid() {

        if (binding.isContent.isChecked()) {
            if (binding.showTable.isChecked()) {
                retrieveDataForRows();
                if (rows.isEmpty()) {
                    Toast.makeText(this, "Please add 1 row data", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (binding.showList.isChecked()) {
                retrieveDataForOptionsList();
                if (optionsList.isEmpty()) {
                    Toast.makeText(this, "Please add 1 option data", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (binding.showImage.isChecked()) {
                if (img == null) {
                    Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (binding.imaegHeading.getEditText().getText().toString().isEmpty()) {
                    Toast.makeText(this, "Image Heading is required", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (binding.subHeading.getEditText().getText().toString().isEmpty()) {
                    Toast.makeText(this, "Sub heading is required", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } else {
            retrieveDataForOptions();
            if (!(options.size()>1)){
                Toast.makeText(this, "Options are required", Toast.LENGTH_SHORT).show();
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

    private void retrieveDataForOptionsList() {
        optionsList.clear();
        for (int i = 0; i < binding.optionsLayout.getChildCount(); i++) {
            View view = binding.optionsLayout.getChildAt(i);
            if (view instanceof RelativeLayout) {
                RelativeLayout textInputLayout = (RelativeLayout) view;
                TextInputLayout customEditText = textInputLayout.findViewById(R.id.addColumn);

                String enteredText = customEditText.getEditText().getText().toString();
                if (!enteredText.isEmpty())
                    optionsList.add(enteredText);
            }
        }
    }

    private void retrieveDataForRows() {
        rows.clear();
        for (int i = 0; i < binding.columnLayout.getChildCount(); i++) {
            View view = binding.columnLayout.getChildAt(i);
            if (view instanceof RelativeLayout) {
                RelativeLayout textInputLayout = (RelativeLayout) view;
                TextInputLayout customEditText = textInputLayout.findViewById(R.id.addColumn);

                String enteredText = customEditText.getEditText().getText().toString();
                if (!enteredText.isEmpty())
                    rows.add(enteredText);
            }
        }
    }

    private void addOptionContent() {
        LayoutInflater inflater = getLayoutInflater();
        View customEditTextLayout = inflater.inflate(R.layout.option_edittext, null);
        TextInputLayout customEditText = customEditTextLayout.findViewById(R.id.addColumn);
        customEditText.setHint("");

        binding.optionsLayout.addView(customEditTextLayout);
    }

    private void addRow() {
        LayoutInflater inflater = getLayoutInflater();
        View customEditTextLayout = inflater.inflate(R.layout.row_edittext, null);
        TextInputLayout customEditText = customEditTextLayout.findViewById(R.id.addColumn);
        customEditText.setHint("Article,Masculine,feminine,Neuter");

        binding.columnLayout.addView(customEditTextLayout);
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
        } else if (requestCode == PICK_AUDIO_REQUEST_CONTENT && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Get the selected audio file URI
                audio = data.getData();
                binding.audioFile.setText("Audio File: " + Constants.getFileName(this, audio));
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                img = data.getData();
                Glide.with(TrialQuestionActivity.this).load(img).placeholder(R.drawable.image).into(binding.imageView);
            }
        }
    }

}