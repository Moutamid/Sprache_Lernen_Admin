 package com.moutamid.sprachelernenadmin.activities;
import com.moutamid.sprachelernenadmin.Stash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

 
import com.google.firebase.database.DataSnapshot;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.adapters.ExerciseAdapters;
import com.moutamid.sprachelernenadmin.adapters.TrialQuestionAdapters;
import com.moutamid.sprachelernenadmin.databinding.ActivityViewTrialBinding;
import com.moutamid.sprachelernenadmin.models.ExerciseModel;
import com.moutamid.sprachelernenadmin.models.TrialContent;

import java.util.ArrayList;

public class ViewTrialActivity extends AppCompatActivity {
    ActivityViewTrialBinding binding;
    ArrayList<TrialContent> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewTrialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("Trial Question");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        binding.contentRC.setHasFixedSize(false);
        binding.contentRC.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        getContent();
    }

    private void getContent() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.TRIAL_QUESTIONS).get()
                .addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        list.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TrialContent model = snapshot.getValue(TrialContent.class);
                            list.add(model);
                        }
//                        TrialQuestionAdapters adapters = new TrialQuestionAdapters(this, list);
//                        binding.contentRC.setAdapter(adapters);
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}