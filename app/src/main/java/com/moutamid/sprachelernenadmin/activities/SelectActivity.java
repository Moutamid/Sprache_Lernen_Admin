package com.moutamid.sprachelernenadmin.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.bottomsheet.LevelSelection;
import com.moutamid.sprachelernenadmin.databinding.ActivitySelectBinding;

import java.util.HashMap;
import java.util.Map;

public class SelectActivity extends AppCompatActivity {
    ActivitySelectBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText(getString(R.string.app));
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        binding.exercise.setOnClickListener(v -> {
            LevelSelection bottomSheetFragment = new LevelSelection(false);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        binding.newExercise.setOnClickListener(v -> {
            LevelSelection bottomSheetFragment = new LevelSelection(true);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        binding.topic.setOnClickListener(v -> startActivity(new Intent(this, AddTopicsActivity.class)));
        binding.writing.setOnClickListener(v -> startActivity(new Intent(this, WritingActivity.class)));
        binding.vocabualry.setOnClickListener(v -> startActivity(new Intent(this, VocabularyActivity.class)));
        binding.viewContent.setOnClickListener(v -> startActivity(new Intent(this, ViewContentActivity.class)));
        binding.viewExercise.setOnClickListener(v -> startActivity(new Intent(this, ViewExerciseActivity.class)));
        binding.trialQuestions.setOnClickListener(v -> startActivity(new Intent(this, TrialQuestionActivity.class)));
        binding.viewTrial.setOnClickListener(v -> startActivity(new Intent(this, ViewTrialActivity.class)));

        binding.payment.setOnClickListener(v -> startActivity(new Intent(this, PaymentActivity.class)));

        binding.pricing.setOnClickListener(v -> {
            getPricing();
        });
    }

    private void getPricing() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.PRICES).get().addOnFailureListener(e -> {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Constants.dismissDialog();
        }).addOnSuccessListener(dataSnapshot -> {
            Constants.dismissDialog();
            if (dataSnapshot.exists()) {
                String year = dataSnapshot.child("YEAR").getValue(String.class);
                String six_month = dataSnapshot.child("SIX_MONTH").getValue(String.class);
                String month = dataSnapshot.child("MONTH").getValue(String.class);
                showDialog(year, six_month, month);
            } else {
                showDialog("130", "100", "90");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }

    private void showDialog(String year, String six_month, String month) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pricing);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        Button update = dialog.findViewById(R.id.update);
        TextInputLayout monthly = dialog.findViewById(R.id.monthly);
        TextInputLayout month_six = dialog.findViewById(R.id.six_month);
        TextInputLayout yearly = dialog.findViewById(R.id.year);

        monthly.getEditText().setText(month);
        month_six.getEditText().setText(six_month);
        yearly.getEditText().setText(year);

        update.setOnClickListener(v -> {
            if (
                    !monthly.getEditText().getText().toString().trim().isEmpty() &&
                    !yearly.getEditText().getText().toString().trim().isEmpty() &&
                    !month_six.getEditText().getText().toString().trim().isEmpty()
            ) {
                Map<String, Object> price = new HashMap<>();
                price.put("MONTH", monthly.getEditText().getText().toString().trim());
                price.put("YEAR", yearly.getEditText().getText().toString().trim());
                price.put("SIX_MONTH", month_six.getEditText().getText().toString().trim());

                Constants.databaseReference().child(Constants.PRICES).updateChildren(price)
                        .addOnSuccessListener(unused -> {
                            dialog.dismiss();
                            Toast.makeText(this, "Prices Updated", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}