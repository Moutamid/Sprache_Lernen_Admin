package com.moutamid.sprachelernenadmin.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.databinding.ActivityWritingEditBinding;
import com.moutamid.sprachelernenadmin.models.WritingModel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WritingEditActivity extends AppCompatActivity {
    ActivityWritingEditBinding binding;
    boolean isGerman = false;
    boolean urduEnabled = true;
    boolean germanEnabled = false;
    Uri germanAudio, audio;
    String germanPath, audioPath;
    private static final int PICK_AUDIO_REQUEST = 1;
    String heading = "", note = "", fileName = "";
    String germHeading = "", germNote = "", germFileName = "";
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWritingEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        binding.toolbar.title.setText("Writing Edit");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        ID = getIntent().getStringExtra(Constants.ID);

        getData();

        binding.german.setOnClickListener(v -> {
            if (!germanEnabled)
                updateViewGerman();
        });

        binding.current.setOnClickListener(v -> {
            if (!urduEnabled)
                updateView();
        });

        binding.audio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST);
        });

        binding.next.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                if (isGerman) {
                    if (germanAudio != null)
                        uploadGerman();
                    else uploadData();
                } else {
                    if (audio != null)
                        uploadUrdu();
                    else uploadData();
                }
            }
        });
    }

    private static String extractFileName(String url) {
        Uri uri = Uri.parse(url);
        String path = uri.getLastPathSegment();
        int lastSlashIndex = path.lastIndexOf('/');
        return (lastSlashIndex != -1) ? path.substring(lastSlashIndex + 1) : path;
    }

    private void getData() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.getLang()).child(Constants.WRITING).child(ID)
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        WritingModel model = dataSnapshot.getValue(WritingModel.class);

                        heading = model.getTopic() != null ? model.getTopic() : "";
                        note = model.getLetter() != null ? model.getLetter() : "";

                        germHeading = model.getGermanTopic() != null ? model.getGermanTopic() : "";
                        germNote = model.getGermanLetter() != null ? model.getGermanLetter() : "";

                        fileName = model.getAudio() != null ? extractFileName(model.getAudio()) : "";
                        audioPath = model.getAudio() != null ? model.getAudio() : "";

                        germFileName = model.getGermanAudio() != null ? extractFileName(model.getGermanAudio()) : "";
                        germanPath = model.getGermanAudio() != null ? model.getGermanAudio() : "";

                        binding.heading.getEditText().setText(heading);
                        binding.note.getEditText().setText(note);
                        binding.audioFile.setText(fileName);
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadUrdu() {
        String name = Constants.getFormattedDate(new Date().getTime()) + "_URDU";
        Constants.storageReference().child("audio").child(name).putFile(audio)
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

    private void uploadGerman() {
        String name = Constants.getFormattedDate(new Date().getTime()) + "_GERMAN";
        Constants.storageReference().child("audio").child(name).putFile(germanAudio)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        germanPath = uri.toString();
                        uploadData();
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ID);
        if (isGerman) {
            map.put("germanTopic", binding.heading.getEditText().getText().toString());
            map.put("germanLetter", binding.note.getEditText().getText().toString());
            map.put("germanAudio", germanPath);
        } else {
            map.put("topic", binding.heading.getEditText().getText().toString());
            map.put("letter", binding.note.getEditText().getText().toString());
            map.put("audio", audioPath);
        }
        Constants.databaseReference().child(Constants.getLang()).child(Constants.WRITING).child(ID).updateChildren(map)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, "Content Added Successfully", Toast.LENGTH_SHORT).show();
//                onBackPressed()
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private boolean valid() {

        if (binding.heading.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.note.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Letter is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateView() {
        isGerman = false;
        urduEnabled = true;
        germanEnabled = false;
        binding.indicator2.setVisibility(View.GONE);
        binding.indicator1.setVisibility(View.VISIBLE);
        germHeading = binding.heading.getEditText().getText().toString();
        germNote = binding.note.getEditText().getText().toString();
        binding.heading.getEditText().setText(heading);
        binding.note.getEditText().setText(note);
        binding.audioFile.setText(fileName);
    }

    private void updateViewGerman() {
        isGerman = true;
        urduEnabled = false;
        germanEnabled = true;
        binding.indicator1.setVisibility(View.GONE);
        binding.indicator2.setVisibility(View.VISIBLE);
        heading = binding.heading.getEditText().getText().toString();
        note = binding.note.getEditText().getText().toString();
        binding.heading.getEditText().setText(germHeading);
        binding.note.getEditText().setText(germNote);
        binding.audioFile.setText(germFileName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                if (isGerman) {
                    germanAudio = data.getData();
                    germFileName = Constants.getFileName(this, germanAudio);
                    binding.audioFile.setText(germFileName);
                } else {
                    audio = data.getData();
                    fileName = Constants.getFileName(this, audio);
                    binding.audioFile.setText(fileName);
                }
            }
        }
    }

}