package com.moutamid.sprachelernenadmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.adapters.PapersAdapter;
import com.moutamid.sprachelernenadmin.databinding.ActivityModelPapersBinding;
import com.moutamid.sprachelernenadmin.models.ModelPaper;

import java.util.ArrayList;
import java.util.Comparator;

public class ModelPapersActivity extends AppCompatActivity {
    ActivityModelPapersBinding binding;
    ArrayList<ModelPaper> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModelPapersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("Model Papers");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        binding.papersRC.setHasFixedSize(false);
        binding.papersRC.setLayoutManager(new LinearLayoutManager(this));

        binding.add.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditPaperActivity.class));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        list = new ArrayList<>();
        Constants.databaseReference().child(Constants.getLang()).child(Constants.MODEL_PAPERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ModelPaper paper = snapshot.getValue(ModelPaper.class);
                        list.add(paper);
                    }
                    list.sort(Comparator.comparing(modelPaper -> modelPaper.name));
                    if (!list.isEmpty()) {
                        binding.loading.setVisibility(View.GONE);
                        binding.papersRC.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(ModelPapersActivity.this, "No Data found", Toast.LENGTH_SHORT).show();
                }
                PapersAdapter adapter = new PapersAdapter(ModelPapersActivity.this, list);
                binding.papersRC.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ModelPapersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}