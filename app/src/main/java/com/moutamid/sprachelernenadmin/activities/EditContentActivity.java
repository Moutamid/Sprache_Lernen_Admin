package com.moutamid.sprachelernenadmin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.databinding.ActivityEditContentBinding;
import com.moutamid.sprachelernenadmin.models.ContentModel;

import java.util.ArrayList;
import java.util.UUID;

public class EditContentActivity extends AppCompatActivity {
    ActivityEditContentBinding binding;
    ArrayList<String> options;
    ArrayList<String> rows;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.title.setText("Edit Content");
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

        ContentModel model = (ContentModel) Stash.getObject(Constants.PASS_CONTENT, ContentModel.class);
        setContent(model);

        binding.addOption.setOnClickListener(v -> addOption());
        binding.addRow.setOnClickListener(v -> addRow());

        binding.next.setOnClickListener(v -> {
            uploadData(model);
        });

    }

    private void setContent(ContentModel model) {
        binding.heading.getEditText().setText(model.getHeading());
        binding.note.getEditText().setText(model.getNote());

        boolean haveRows = model.isHaveTable();
        boolean hasOptions = model.isHasOptions();

        binding.showList.setChecked(hasOptions);
        binding.showTable.setChecked(haveRows);

        if (hasOptions) {
            binding.optionsLayout.removeAllViews();
            for (int i=0; i<model.getOptions().size(); i++){
                addOption();
            }
            for (int i = 0; i < binding.optionsLayout.getChildCount(); i++) {
                View view = binding.optionsLayout.getChildAt(i);
                if (view instanceof RelativeLayout) {
                    RelativeLayout textInputLayout = (RelativeLayout) view;
                    TextInputLayout customEditText = textInputLayout.findViewById(R.id.addColumn);

                    customEditText.getEditText().setText(model.getOptions().get(i));
                }
            }
        }
        if (haveRows) {
            binding.columnLayout.removeAllViews();
            for (int i=0; i<model.getRows().size(); i++){
                addRow();
            }
            for (int i = 0; i < binding.columnLayout.getChildCount(); i++) {
                View view = binding.columnLayout.getChildAt(i);
                if (view instanceof RelativeLayout) {
                    RelativeLayout textInputLayout = (RelativeLayout) view;
                    TextInputLayout customEditText = textInputLayout.findViewById(R.id.addColumn);

                    customEditText.getEditText().setText(model.getRows().get(i));
                }
            }
        }

    }

    private void uploadData(ContentModel model) {
        Constants.showDialog();
        boolean haveRows = binding.showTable.isChecked();
        boolean hasOptions = binding.showList.isChecked();

        if (haveRows) retrieveDataForRows();
        if (hasOptions) retrieveDataForOptions();

        ContentModel contentModel = new ContentModel(model.getID(), model.getTopicsModel(),
                binding.heading.getEditText().getText().toString(),
                binding.note.getEditText().getText().toString(), hasOptions, haveRows, options, rows);

        Constants.databaseReference().child(Constants.getLang()).child(Constants.CONTENT).child(model.getID()).setValue(contentModel)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(EditContentActivity.this, "Content Updated Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(EditContentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void retrieveDataForOptions() {
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