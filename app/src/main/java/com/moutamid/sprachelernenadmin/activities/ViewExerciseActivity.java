 package com.moutamid.sprachelernenadmin.activities;
import com.moutamid.sprachelernenadmin.Stash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

 
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.adapters.ContentAdapters;
import com.moutamid.sprachelernenadmin.adapters.ExerciseAdapters;
import com.moutamid.sprachelernenadmin.databinding.ActivityViewExerciseBinding;
import com.moutamid.sprachelernenadmin.models.ContentModel;
import com.moutamid.sprachelernenadmin.models.ExerciseModel;

import java.util.ArrayList;

public class ViewExerciseActivity extends AppCompatActivity {
    ActivityViewExerciseBinding binding;
    ArrayList<ExerciseModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("View Exercise");
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
        Constants.databaseReference().child(Constants.getLang()).child(Constants.EXERCISE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Constants.dismissDialog();
                if (dataSnapshot.exists()) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                            for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                                for (DataSnapshot snapshot4 : snapshot3.getChildren()) {
                                    ExerciseModel model = snapshot4.getValue(ExerciseModel.class);
                                    list.add(model);
                                }
                            }
                        }
                    }
                    ExerciseAdapters adapters = new ExerciseAdapters(ViewExerciseActivity.this, list);
                    binding.contentRC.setAdapter(adapters);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Constants.dismissDialog();
                Toast.makeText(ViewExerciseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}