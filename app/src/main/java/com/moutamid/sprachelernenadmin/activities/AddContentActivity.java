package com.moutamid.sprachelernenadmin.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityAddContentBinding;
import com.moutamid.sprachelernenadmin.models.ContentModel;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import java.util.ArrayList;

public class AddContentActivity extends AppCompatActivity {
    ActivityAddContentBinding binding;
    ArrayList<String> options;
    ArrayList<String> rows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.title.setText("Add Content");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        options = new ArrayList<>();
        rows = new ArrayList<>();

        binding.showTable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int v = isChecked ? View.VISIBLE : View.GONE;
            binding.tableLayout.setVisibility(v);
            if (!isChecked) {
                binding.columnLayout.removeAllViews();
                addRow();
            }
        });

        binding.showList.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int v = isChecked ? View.VISIBLE : View.GONE;
            binding.options.setVisibility(v);
            if (!isChecked) {
                binding.optionsLayout.removeAllViews();
                addOption();
            }
        });

        addOption();
        addRow();

        binding.addOption.setOnClickListener(v -> addOption());
        binding.addRow.setOnClickListener(v -> addRow());

        binding.next.setOnClickListener(v -> {
            if (valid())
                uploadData();
        });

    }

    private boolean valid() {
        if (binding.showTable.isChecked()) {
            retrieveDataForRows();
            if (!(rows.size()>=1)) {
                Toast.makeText(this, "Please add 1 row data", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (binding.showList.isChecked()) {
            retrieveDataForOptions();
            if (!(options.size()>=1)) {
                Toast.makeText(this, "Please add 1 option data", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (binding.heading.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Heading is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.note.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Note is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadData() {
        Constants.showDialog();
        boolean haveRows = binding.showTable.isChecked();
        boolean hasOptions = binding.showList.isChecked();

        if (haveRows) retrieveDataForRows();
        if (hasOptions) retrieveDataForOptions();

        TopicsModel topicsModel = (TopicsModel) Stash.getObject(Constants.PASS, TopicsModel.class);

        ContentModel model = new ContentModel(topicsModel.getID(), topicsModel,
                binding.heading.getEditText().getText().toString(),
                binding.note.getEditText().getText().toString(), hasOptions, haveRows, options, rows);

        Constants.databaseReference().child(Constants.getLang()).child(Constants.CONTENT).child(topicsModel.getContentType()).child(model.getID()).setValue(model)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(AddContentActivity.this, "Content Added Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(AddContentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void retrieveDataForOptions() {
        options.clear();
        for (int i = 0; i < binding.optionsLayout.getChildCount(); i++) {
            View view = binding.optionsLayout.getChildAt(i);
            if (view instanceof RelativeLayout) {
                RelativeLayout textInputLayout = (RelativeLayout) view;
                TextInputLayout customEditText = textInputLayout.findViewById(R.id.addColumn);

                String enteredText = customEditText.getEditText().getText().toString();
                if (!enteredText.isEmpty())
                    options.add(enteredText);
            }
        }
    }

    private void retrieveDataForRows() {
        rows.clear();
        for (int i = 0; i < binding.columnLayout.getChildCount(); i++) {
            View view = binding.columnLayout.getChildAt(i);
            if (view instanceof RelativeLayout) {
                RelativeLayout textInputLayout = (RelativeLayout) view;
                TextInputLayout customEditText = textInputLayout.findViewById(R.id.addColumn);

                String enteredText = customEditText.getEditText().getText().toString();
                if (!enteredText.isEmpty())
                    rows.add(enteredText);
            }
        }
    }

    private void addOption() {
        LayoutInflater inflater = getLayoutInflater();
        View customEditTextLayout = inflater.inflate(R.layout.option_edittext, null);
        TextInputLayout customEditText = customEditTextLayout.findViewById(R.id.addColumn);
        customEditText.setHint("");

        binding.optionsLayout.addView(customEditTextLayout);
    }

    private void addRow() {
        LayoutInflater inflater = getLayoutInflater();
        View customEditTextLayout = inflater.inflate(R.layout.row_edittext, null);
        TextInputLayout customEditText = customEditTextLayout.findViewById(R.id.addColumn);
        customEditText.setHint("Article,Masculine,feminine,Neuter");

        binding.columnLayout.addView(customEditTextLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }

}