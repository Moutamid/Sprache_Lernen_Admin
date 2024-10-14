package com.moutamid.sprachelernenadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.moutamid.sprachelernenadmin.activities.SelectActivity;
import com.moutamid.sprachelernenadmin.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("admin@b1pruefung.com", "b1admin").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                binding.urdu.setOnClickListener(v -> {
                    Stash.put(Constants.SELECT, Constants.URDU);
                    startActivity(new Intent(MainActivity.this, SelectActivity.class));
                });
                binding.english.setOnClickListener(v -> showWarning());
                binding.persian.setOnClickListener(v -> showWarning());
            }
        });
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