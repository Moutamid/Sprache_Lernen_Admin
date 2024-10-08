 package com.moutamid.sprachelernenadmin.activities;
import com.moutamid.sprachelernenadmin.Stash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

 
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.adapters.ModelTopicsAdapter;
import com.moutamid.sprachelernenadmin.databinding.ActivityVocabularyPapersBinding;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class VocabularyActivity extends AppCompatActivity {
    ActivityVocabularyPapersBinding binding;
    ArrayList<TopicsModel> list;
    ModelTopicsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVocabularyPapersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("Topics List");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        list = new ArrayList<>();

        binding.topics.setLayoutManager(new LinearLayoutManager(this));
        binding.topics.setHasFixedSize(false);

        binding.fab.setOnClickListener(v -> showDialog());

        binding.search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void showDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_topic_model);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        TextInputLayout topic = dialog.findViewById(R.id.topic);
        Button complete = dialog.findViewById(R.id.complete);

        complete.setOnClickListener(v -> {
            String topicName = topic.getEditText().getText().toString();
            if (!topicName.isEmpty()) {
                dialog.dismiss();
                Constants.showDialog();

                TopicsModel model = new TopicsModel(UUID.randomUUID().toString(), topicName, "Vocabulary");
                Constants.databaseReference().child(Constants.getLang()).child(Constants.VOCABULARY).child(Constants.TOPICS).child(model.getID()).setValue(model)
                        .addOnSuccessListener(unused -> {
                            Constants.dismissDialog();
                            Toast.makeText(VocabularyActivity.this, "Topic Added Successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Constants.dismissDialog();
                            Toast.makeText(VocabularyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                topic.setErrorEnabled(true);
                topic.setError("Topic name is empty");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        String name = Stash.getString(Constants.SELECT, Constants.URDU);
        Constants.databaseReference().child(name).child(Constants.VOCABULARY).child(Constants.TOPICS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Constants.dismissDialog();
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        TopicsModel topicsModel = dataSnapshot.getValue(TopicsModel.class);
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

                    adapter = new ModelTopicsAdapter(VocabularyActivity.this, list);
                    binding.topics.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Constants.dismissDialog();
                Toast.makeText(VocabularyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}