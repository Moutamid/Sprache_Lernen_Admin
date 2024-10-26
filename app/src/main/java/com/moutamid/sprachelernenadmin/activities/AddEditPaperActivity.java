package com.moutamid.sprachelernenadmin.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityAddEditPaperBinding;
import com.moutamid.sprachelernenadmin.models.ModelPaper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddEditPaperActivity extends AppCompatActivity {
    private static final int PICK_PDF_REQUEST_CODE = 1001;
    private static final int PICK_VOICE_REQUEST_CODE = 1002;
    ActivityAddEditPaperBinding binding;
    Uri pdfUri;
    ArrayList<Uri> voices;
    ArrayList<String> audios_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditPaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);

        binding.toolbar.title.setText("Model Paper");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        voices = new ArrayList<>();
        audios_url = new ArrayList<>();

        binding.addOption.setOnClickListener(v -> pickVoice());

        binding.pdf.setOnClickListener(v -> pickPDF());

        binding.upload.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                uploadPDF();
            }
        });
    }

    private void uploadPDF() {
        Constants.storageReference().child(Constants.MODEL_PAPERS).child("pdfs")
                .child(binding.pdfFile.getText().toString())
                .putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        pdf = uri.toString();
                        upload();
                    });
                });
    }

    String pdf;

    private void upload() {
        if (voices.isEmpty()) {
            uploadData();
        } else {
            uploadAudios(0);
        }
    }

    private void uploadData() {
        ModelPaper paper = new ModelPaper();
        paper.id = UUID.randomUUID().toString();
        paper.link = pdf;
        paper.name = binding.name.getEditText().getText().toString().trim();
        paper.voices = new ArrayList<>(audios_url);

        Constants.databaseReference().child(Constants.getLang()).child(Constants.MODEL_PAPERS)
                .child(paper.id).setValue(paper)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, "Data uploaded", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> {
                   Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadAudios(int pos) {
        Constants.storageReference().child(Constants.MODEL_PAPERS).child("audios")
                .child(new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault()).format(new Date()))
                .putFile(voices.get(pos))
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        audios_url.add(uri.toString());
                        if (pos == voices.size() - 1) {
                            uploadData();
                        } else {
                            uploadAudios(pos + 1);
                        }
                    });
                });
    }

    private boolean valid() {
        if (binding.name.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pdfUri == null) {
            Toast.makeText(this, "Select PDF file", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void pickPDF() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_PDF_REQUEST_CODE);
    }

    private void pickVoice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_VOICE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_PDF_REQUEST_CODE) {
                pdfUri = data.getData();
                binding.pdfFile.setText(Constants.getFileName(this, pdfUri));
            } else if (requestCode == PICK_VOICE_REQUEST_CODE) {
                Uri audioUri = data.getData();
                voices.add(audioUri);
                updateView();
            }
        }
    }

    private void updateView() {
        binding.optionsLayout.removeAllViews();
        for (Uri audio : voices) {
            LayoutInflater inflater = getLayoutInflater();
            View customEditTextLayout = inflater.inflate(R.layout.voices, null);
            TextView text = customEditTextLayout.findViewById(R.id.text);
            MaterialCardView delete = customEditTextLayout.findViewById(R.id.delete);

            text.setText(Constants.getFileName(this, audio));
            delete.setOnClickListener(v -> {
                voices.remove(audio);
                updateView();
            });
            binding.optionsLayout.addView(customEditTextLayout);
        }
    }
}