package com.moutamid.sprachelernenadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.fxn.stash.Stash;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.sprachelernenadmin.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        binding.urdu.setOnClickListener(v -> {
            Stash.put(Constants.SELECT, Constants.URDU);
            startActivity(new Intent(this, SelectActivity.class));
        });

        binding.english.setOnClickListener(v -> showWarning());
        binding.persian.setOnClickListener(v -> showWarning());

    }

    private void showWarning() {
        new MaterialAlertDialogBuilder(this)
                .setCancelable(true)
                .setTitle("Language Unavailable")
                .setMessage("We're sorry, but the selected language is not available at the moment.")
                .setPositiveButton("OK", ((dialog, which) -> dialog.dismiss()))
                .show();
    }

}