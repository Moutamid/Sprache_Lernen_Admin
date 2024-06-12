package com.moutamid.sprachelernenadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.adapters.ModelContentAdapters;
import com.moutamid.sprachelernenadmin.databinding.ActivityVocabularyContentBinding;
import com.moutamid.sprachelernenadmin.models.VocabularyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VocabularyContentActivity extends AppCompatActivity {
    ActivityVocabularyContentBinding binding;
    String ID;
    ArrayList<VocabularyModel> list;
    ModelContentAdapters adapters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVocabularyContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ID = getIntent().getStringExtra(Constants.TOPICS);

        binding.toolbar.title.setText("Topics List");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        list = new ArrayList<>();

        binding.topics.setLayoutManager(new LinearLayoutManager(this));
        binding.topics.setHasFixedSize(false);

        binding.search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapters.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
        Constants.databaseReference().child(Constants.getLang()).child(Constants.VOCABULARY).child(Constants.CONTENT).child(ID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Constants.dismissDialog();
                        if (dataSnapshot.exists()) {
                            list.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                VocabularyModel model = snapshot.getValue(VocabularyModel.class);
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
                            if (list.get(0).getPos() == 0) {
                                list.sort(Comparator.comparing(VocabularyModel::getName));
                            } else {
                                list.sort(Comparator.comparing(VocabularyModel::getPos));
                            }
                            adapters = new ModelContentAdapters(VocabularyContentActivity.this, list);
                            binding.topics.setAdapter(adapters);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Constants.dismissDialog();
                        Toast.makeText(VocabularyContentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}