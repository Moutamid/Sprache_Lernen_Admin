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
import com.moutamid.sprachelernenadmin.databinding.ActivityViewContentBinding;
import com.moutamid.sprachelernenadmin.models.ContentModel;

import java.util.ArrayList;

public class ViewContentActivity extends AppCompatActivity {
    ActivityViewContentBinding binding;
    ArrayList<ContentModel> list;
    ContentAdapters adapters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("View Content");
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
        Constants.databaseReference().child(Constants.getLang()).child(Constants.CONTENT).child(Constants.Speaking).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Constants.dismissDialog();
                if (dataSnapshot.exists()) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                            ContentModel model = snapshot2.getValue(ContentModel.class);
                            list.add(model);
                        }
                    }
                    adapters = new ContentAdapters(ViewContentActivity.this, list);
                    binding.contentRC.setAdapter(adapters);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Constants.dismissDialog();
                Toast.makeText(ViewContentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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


}