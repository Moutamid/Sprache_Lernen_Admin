package com.moutamid.sprachelernenadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.adapters.ContentAdapters;
import com.moutamid.sprachelernenadmin.adapters.ModelContentAdapters;
import com.moutamid.sprachelernenadmin.databinding.ActivityModelContentBinding;
import com.moutamid.sprachelernenadmin.models.ContentModel;
import com.moutamid.sprachelernenadmin.models.ModelContent;

import java.util.ArrayList;
import java.util.Collections;

public class ModelContentActivity extends AppCompatActivity {
    ActivityModelContentBinding binding;
    String ID;
    ArrayList<ModelContent> list;
    ModelContentAdapters adapters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModelContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ID = getIntent().getStringExtra(Constants.TOPICS);

        binding.toolbar.title.setText("Topics List");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        list = new ArrayList<>();

        binding.topics.setLayoutManager(new LinearLayoutManager(this));
        binding.topics.setHasFixedSize(false);

        binding.fab.setOnClickListener(v -> startActivity(new Intent(this, AddModelContentActivity.class).putExtra(Constants.TOPICS, ID)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        getContent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapters != null) {
            adapters.releaseMediaPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapters != null) {
            adapters.releaseMediaPlayer();
        }
    }

    private void getContent() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.getLang()).child(Constants.MODEL_PAPERS).child(Constants.CONTENT).child(ID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Constants.dismissDialog();
                        if (dataSnapshot.exists()) {
                            list.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ModelContent model = snapshot.getValue(ModelContent.class);
                                list.add(model);
                            }

                            if (list.size() > 0){
                                Collections.reverse(list);
                                binding.topics.setVisibility(View.VISIBLE);
                                binding.noLayout.setVisibility(View.GONE);
                            } else {
                                binding.topics.setVisibility(View.GONE);
                                binding.noLayout.setVisibility(View.VISIBLE);
                            }

                            adapters = new ModelContentAdapters(ModelContentActivity.this, list);
                            binding.topics.setAdapter(adapters);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Constants.dismissDialog();
                        Toast.makeText(ModelContentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}