 package com.moutamid.sprachelernenadmin.activities;
import com.moutamid.sprachelernenadmin.Stash;

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
import com.moutamid.sprachelernenadmin.adapters.TopicsAdapter;
import com.moutamid.sprachelernenadmin.adapters.WritingAdapter;
import com.moutamid.sprachelernenadmin.databinding.ActivityWritingBinding;
import com.moutamid.sprachelernenadmin.models.TopicsModel;
import com.moutamid.sprachelernenadmin.models.WritingModel;

import java.util.ArrayList;
import java.util.Collections;

public class WritingActivity extends AppCompatActivity {
    ActivityWritingBinding binding;
    ArrayList<WritingModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWritingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);
        binding.toolbar.title.setText("Writing");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        list = new ArrayList<>();

        binding.topics.setLayoutManager(new LinearLayoutManager(this));
        binding.topics.setHasFixedSize(false);

        binding.fab.setOnClickListener(v -> startActivity(new Intent(this, WrittingContentActivity.class)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        Constants.databaseReference().child(Constants.getLang()).child(Constants.WRITING)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Constants.dismissDialog();
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                WritingModel topicsModel = dataSnapshot.getValue(WritingModel.class);
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

                            WritingAdapter adapter = new WritingAdapter(WritingActivity.this, list);
                            binding.topics.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Constants.dismissDialog();
                        Toast.makeText(WritingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}