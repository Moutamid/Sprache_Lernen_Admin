package com.moutamid.sprachelernenadmin.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityAddModelContentBinding;
import com.moutamid.sprachelernenadmin.models.VocabularyModel;

import java.util.Date;
import java.util.UUID;

public class AddModelContentActivity extends AppCompatActivity {
    ActivityAddModelContentBinding binding;
    String ID;
    Uri image, audio, germanAudio;
    private static final int PICK_AUDIO_REQUEST = 1;
    private static final int PICK_GERMAN_AUDIO_REQUEST = 2;
    private static final int PICK_IMAGE_REQUEST = 3;
    String imagePath, audioPath, germanAudioPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddModelContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        ID = getIntent().getStringExtra(Constants.TOPICS);

        binding.toolbar.title.setText("Model Content");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        binding.audio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST);
        });
        binding.german.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_GERMAN_AUDIO_REQUEST);
        });

        binding.upload.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                uploadImage();
            }
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
                        Constants.storageReference().child("audio").child(Constants.getFormattedDate(new Date().getTime())).putFile(germanAudio)
                                .addOnSuccessListener(taskSnapshot2 -> {
                                    taskSnapshot2.getStorage().getDownloadUrl().addOnSuccessListener(uri2 -> {
                                        germanAudioPath = uri2.toString();
                                        uploadData();
                                    });
                                }).addOnFailureListener(e -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData() {
        VocabularyModel vocabularyModel = new VocabularyModel(
                UUID.randomUUID().toString(), ID,
                binding.name.getEditText().getText().toString(),
                binding.nameGerman.getEditText().getText().toString(),
                imagePath, audioPath, germanAudioPath, Integer.parseInt(binding.pos.getEditText().getText().toString())
        );
        Constants.databaseReference().child(Constants.getLang()).child(Constants.VOCABULARY).child(Constants.CONTENT).child(ID).child(vocabularyModel.getID()).setValue(vocabularyModel)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, "Content Added Successfully", Toast.LENGTH_SHORT).show();
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
        if (germanAudio == null) {
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
                binding.audioFile.setText("Urdu Audio File: " + Constants.getFileName(this,audio));
            }
        } else if (requestCode == PICK_GERMAN_AUDIO_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Get the selected audio file URI
                germanAudio = data.getData();
                binding.audioFileGerman.setText("German Audio File: " + Constants.getFileName(this,germanAudio));
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Get the selected audio file URI
                image = data.getData();
                Glide.with(AddModelContentActivity.this).load(image).placeholder(R.drawable.image).into(binding.imageView);
            }
        }
    }

}