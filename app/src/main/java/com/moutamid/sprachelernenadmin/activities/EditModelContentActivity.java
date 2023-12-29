package com.moutamid.sprachelernenadmin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityEditModelContentBinding;
import com.moutamid.sprachelernenadmin.models.ModelContent;

import java.util.Date;
import java.util.Objects;

public class EditModelContentActivity extends AppCompatActivity {
    ActivityEditModelContentBinding binding;
    ModelContent model;
    Uri image, audio;
    private static final int PICK_AUDIO_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    String imagePath, audioPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditModelContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        model = (ModelContent) Stash.getObject(Constants.PASS_CONTENT, ModelContent.class);

        binding.toolbar.title.setText("Model Content");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        updateUI();

        binding.audio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST);
        });

        binding.upload.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                uploadImage();
            }
        });

        binding.image.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
        });


    }

    private void updateUI() {
        binding.name.getEditText().setText(model.getName());
        binding.nameGerman.getEditText().setText(model.getNameGerman());
        Glide.with(EditModelContentActivity.this).load(model.getImage()).placeholder(R.drawable.image).into(binding.imageView);

        String fileName = extractFileName(model.getAudio());

        binding.audioFile.setText(fileName + ".mp3");

    }

    private static String extractFileName(String url) {
        Uri uri = Uri.parse(url);
        String path = uri.getLastPathSegment();
        int lastSlashIndex = path.lastIndexOf('/');
        return (lastSlashIndex != -1) ? path.substring(lastSlashIndex + 1) : path;
    }


    private void uploadImage() {
        Constants.storageReference().child("images").child(Constants.getFormattedDate(new Date().getTime())).putFile(image)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        imagePath = uri.toString();
                        uploadAudio();
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
                        uploadData();
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData() {
        ModelContent modelContent = new ModelContent(
                model.getID(), model.getTopicID(),
                binding.name.getEditText().getText().toString(),
                binding.nameGerman.getEditText().getText().toString(),
                imagePath, audioPath
        );
        Constants.databaseReference().child(Constants.getLang()).child(Constants.VOCABULARY).child(Constants.CONTENT).child(model.getTopicID()).child(modelContent.getID()).setValue(modelContent)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, "Content Updated Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private boolean valid() {
        if (image == null) {
            Toast.makeText(this, "Upload an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (audio == null) {
            Toast.makeText(this, "Upload an audio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.name.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.nameGerman.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "German Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Get the selected audio file URI
                audio = data.getData();
                binding.audioFile.setText(getFileName(audio));
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Get the selected audio file URI
                image = data.getData();
                Glide.with(EditModelContentActivity.this).load(image).placeholder(R.drawable.image).into(binding.imageView);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

}