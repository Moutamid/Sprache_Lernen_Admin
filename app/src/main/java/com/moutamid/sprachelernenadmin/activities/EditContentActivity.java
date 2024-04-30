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
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityEditContentBinding;
import com.moutamid.sprachelernenadmin.models.ContentModel;

import java.util.ArrayList;
import java.util.Date;

public class EditContentActivity extends AppCompatActivity {
    ActivityEditContentBinding binding;
    ArrayList<String> options;
    ArrayList<String> rows;
    String image = "", audio = "";
    String imageHeading = "", subHeading = "";
    Uri img, aud;
    private static final int PICK_AUDIO_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    ContentModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.title.setText("Edit Content");
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

        binding.showImage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int v = isChecked ? View.VISIBLE : View.GONE;
            binding.imageLayout.setVisibility(v);
        });

        model = (ContentModel) Stash.getObject(Constants.PASS_CONTENT, ContentModel.class);
        setContent(model);

        binding.addOption.setOnClickListener(v -> addOption());
        binding.addRow.setOnClickListener(v -> addRow());

        binding.next.setOnClickListener(v -> {
            if (valid()) {
                if (aud != null) {
                    uploadAudio();
                } else uploadData();
            }
        });

        binding.audio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST);
        });

        binding.image.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .galleryOnly()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start(PICK_IMAGE_REQUEST);
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
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
            if (!(rows.size() >= 1)) {
                Toast.makeText(this, "Please add 1 row data", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (binding.showList.isChecked()) {
            retrieveDataForOptions();
            if (!(options.size() >= 1)) {
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
        if (aud == null) {
            Toast.makeText(this, "Audio file is required", Toast.LENGTH_SHORT).show();
            return false;
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
        return true;
    }

    private void setContent(ContentModel model) {
        binding.heading.getEditText().setText(model.getHeading());
        binding.note.getEditText().setText(model.getNote());
        binding.count.getEditText().setText(model.getPos()+"");

        boolean haveRows = model.isHaveTable();
        boolean hasOptions = model.isHasOptions();

        binding.showList.setChecked(hasOptions);
        binding.showTable.setChecked(haveRows);
        binding.showImage.setChecked(!model.getImage().isEmpty());

        image = model.getImage();
        audio = model.getAudio();

        Glide.with(EditContentActivity.this).load(image).placeholder(R.drawable.image).into(binding.imageView);

        binding.imaegHeading.getEditText().setText(model.getImageHeading());
        binding.subHeading.getEditText().setText(model.getSubHeading());


        String fileName = Constants.extractFileName(model.getAudio()) + ".mp3";
        binding.audioFile.setText(fileName);

        if (hasOptions) {
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
        if (haveRows) {
            binding.columnLayout.removeAllViews();
            for (int i = 0; i < model.getRows().size(); i++) {
                addRow();
            }
            for (int i = 0; i < binding.columnLayout.getChildCount(); i++) {
                View view = binding.columnLayout.getChildAt(i);
                if (view instanceof RelativeLayout) {
                    RelativeLayout textInputLayout = (RelativeLayout) view;
                    TextInputLayout customEditText = textInputLayout.findViewById(R.id.addColumn);

                    customEditText.getEditText().setText(model.getRows().get(i));
                }
            }
        }

    }

    private void uploadData() {
        Constants.showDialog();
        boolean haveRows = binding.showTable.isChecked();
        boolean hasOptions = binding.showList.isChecked();

        if (haveRows) retrieveDataForRows();
        if (hasOptions) retrieveDataForOptions();

        if (binding.showImage.isChecked()) {
            imageHeading = binding.imaegHeading.getEditText().getText().toString();
            subHeading = binding.subHeading.getEditText().getText().toString();
        }

        ContentModel contentModel = new ContentModel(model.getID(), model.getTopicsModel(),
                binding.heading.getEditText().getText().toString(),
                binding.note.getEditText().getText().toString(), image, audio, imageHeading, subHeading, hasOptions, haveRows, options, rows,
                Integer.parseInt(binding.count.getEditText().getText().toString()));

        Constants.databaseReference().child(Constants.getLang()).child(Constants.CONTENT).child(model.getTopicsModel().getContentType()).child(model.getTopicsModel().getID()).child(model.getID()).setValue(contentModel)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(EditContentActivity.this, "Content Updated Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(EditContentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void retrieveDataForOptions() {
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
                binding.audioFile.setText("Audio File: " + Constants.getFileName(this, aud));
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                img = data.getData();
                Glide.with(EditContentActivity.this).load(img).placeholder(R.drawable.image).into(binding.imageView);
            }
        }
    }

}