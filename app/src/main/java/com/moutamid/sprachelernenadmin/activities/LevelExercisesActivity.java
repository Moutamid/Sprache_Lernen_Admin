 package com.moutamid.sprachelernenadmin.activities;
import com.moutamid.sprachelernenadmin.Stash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

 
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.adapters.ExListAdapter;
import com.moutamid.sprachelernenadmin.adapters.ExerciseListAdapter;
import com.moutamid.sprachelernenadmin.adapters.TopicsAdapter;
import com.moutamid.sprachelernenadmin.databinding.ActivityLevelExercisesBinding;
import com.moutamid.sprachelernenadmin.models.Exercise;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

public class LevelExercisesActivity extends AppCompatActivity {
    ActivityLevelExercisesBinding binding;
    ArrayList<Exercise> list;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLevelExercisesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = getIntent().getStringExtra("name");

        binding.toolbar.title.setText("Exercise List");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        list = new ArrayList<>();

        binding.topics.setLayoutManager(new LinearLayoutManager(this));
        binding.topics.setHasFixedSize(false);

        binding.fab.setOnClickListener(v -> showDialog());

    }

    private void showDialog() {
        String lang = Stash.getString(Constants.SELECT, Constants.URDU);
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_exercise);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        TextInputLayout topic = dialog.findViewById(R.id.topic);
        TextInputLayout count = dialog.findViewById(R.id.count);
        Button complete = dialog.findViewById(R.id.complete);
        ChipGroup chipGroup = dialog.findViewById(R.id.contentType);

        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(name)){
                chip.setChecked(true);
            }
        }

        complete.setOnClickListener(v -> {
            String topicName = topic.getEditText().getText().toString();
            String countEx = count.getEditText().getText().toString();
            if (!topicName.isEmpty() && !countEx.isEmpty()) {
                dialog.dismiss();
                Constants.showDialog();

                String s = "";

                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) chipGroup.getChildAt(i);
                    if (chip.isChecked()){
                        s = chip.getText().toString();
                    }
                }

                Exercise model = new Exercise(UUID.randomUUID().toString(), s, topicName, Integer.parseInt(countEx));
                Constants.databaseReference().child(lang).child(Constants.EXERCISE_LIST).child(s).child(model.getID()).setValue(model)
                        .addOnSuccessListener(unused -> {
                            Constants.dismissDialog();
                            Toast.makeText(LevelExercisesActivity.this, "Exercise Added Successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Constants.dismissDialog();
                            Toast.makeText(LevelExercisesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        Constants.databaseReference().child(Constants.getLang()).child(Constants.EXERCISE_LIST).child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Constants.dismissDialog();
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                        Exercise topicsModel = dataSnapshot2.getValue(Exercise.class);
                        list.add(topicsModel);
                    }

                    if (list.size() > 0){
                        Collections.reverse(list);
                        binding.topics.setVisibility(View.VISIBLE);
                        binding.noLayout.setVisibility(View.GONE);
                    } else {
                        binding.topics.setVisibility(View.GONE);
                        binding.noLayout.setVisibility(View.VISIBLE);
                    }

                    Collections.sort(list, new Comparator<Exercise>() {
                        @Override
                        public int compare(Exercise o1, Exercise o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });

                    ExerciseListAdapter adapter = new ExerciseListAdapter(LevelExercisesActivity.this, list);
                    binding.topics.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Constants.dismissDialog();
                Toast.makeText(LevelExercisesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}