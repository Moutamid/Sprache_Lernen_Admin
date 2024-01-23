package com.moutamid.sprachelernenadmin.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityAddContentBinding;
import com.moutamid.sprachelernenadmin.models.ContentModel;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class AddContentActivity extends AppCompatActivity {
    ActivityAddContentBinding binding;
    ArrayList<String> options;
    ArrayList<String> rows;
    String image = "", audio = "";
    Uri img, aud;
    private static final int PICK_AUDIO_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.title.setText("Add Content");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        options = new ArrayList<>();
        rows = new ArrayList<>();

        binding.showTable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int v = isChecked ? View.VISIBLE : View.GONE;
            binding.tableLayout.setVisibility(v);
            if (!isChecked) {
                binding.columnLayout.removeAllViews();
                addRow();
            }
        });

        binding.showList.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int v = isChecked ? View.VISIBLE : View.GONE;
            binding.options.setVisibility(v);
            if (!isChecked) {
                binding.optionsLayout.removeAllViews();
                addOption();
            }
        });

        binding.audio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST);
        });

        binding.image.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
        });


        addOption();
        addRow();

        binding.addOption.setOnClickListener(v -> addOption());
        binding.addRow.setOnClickListener(v -> addRow());

        binding.next.setOnClickListener(v -> {
            if (valid())
                uploadAudio();
        });

    }

    private void uploadAudio() {
        Constants.showDialog();
        Constants.storageReference().child("audio").child(Constants.getFormattedDate(new Date().getTime())).putFile(aud)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        audio = uri.toString();
                        if (img != null) {
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

    private boolean valid() {
        if (binding.showTable.isChecked()) {
            retrieveDataForRows();
            if (!(rows.size()>=1)) {
                Toast.makeText(this, "Please add 1 row data", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (binding.showList.isChecked()) {
            retrieveDataForOptions();
            if (!(options.size()>=1)) {
                Toast.makeText(this, "Please add 1 option data", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (binding.heading.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Heading is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.note.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Note is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (aud==null){
            Toast.makeText(this, "Audio file is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadData() {
        boolean haveRows = binding.showTable.isChecked();
        boolean hasOptions = binding.showList.isChecked();

        if (haveRows) retrieveDataForRows();
        if (hasOptions) retrieveDataForOptions();

        TopicsModel topicsModel = (TopicsModel) Stash.getObject(Constants.PASS, TopicsModel.class);
        ContentModel model = new ContentModel(UUID.randomUUID().toString(), topicsModel,
                binding.heading.getEditText().getText().toString(),
                binding.note.getEditText().getText().toString(), image, audio, hasOptions, haveRows, options, rows);

        Constants.databaseReference().child(Constants.getLang()).child(Constants.CONTENT).child(topicsModel.getContentType()).child(topicsModel.getID()).child(model.getID()).setValue(model)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(AddContentActivity.this, "Content Added Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(AddContentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void addOption() {
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
                // Get the selected audio file URI
                aud = data.getData();
                binding.audioFile.setText("Audio File: " + Constants.getFileName(this,aud));
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                img = data.getData();
                Glide.with(AddContentActivity.this).load(img).placeholder(R.drawable.image).into(binding.imageView);
            }
        }
    }

}