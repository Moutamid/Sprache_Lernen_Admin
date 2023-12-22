package com.moutamid.sprachelernenadmin.bottomsheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxn.stash.Stash;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.activities.AddExerciseActivity;
import com.moutamid.sprachelernenadmin.activities.LevelExercisesActivity;

public class LevelSelection extends BottomSheetDialogFragment {
    MaterialCardView a1,a2,b1;
    boolean check;

    public LevelSelection(boolean check) {
        this.check = check;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.level_selection, container, false);

        a1 = view.findViewById(R.id.A1);
        a2 = view.findViewById(R.id.A2);
        b1 = view.findViewById(R.id.B1);

        a1.setOnClickListener(v -> {
            dismiss();
            if (check){
                startActivity(new Intent(requireContext(), LevelExercisesActivity.class).putExtra("name", "A1"));
            } else {
                ExerciseSelection exerciseSelection = new ExerciseSelection("A1");
                exerciseSelection.show(requireActivity().getSupportFragmentManager(), exerciseSelection.getTag());
            }
        });

        a2.setOnClickListener(v -> {
            dismiss();
            if (check){
                startActivity(new Intent(requireContext(), LevelExercisesActivity.class).putExtra("name", "A2"));
            } else {
                ExerciseSelection exerciseSelection = new ExerciseSelection("A2");
                exerciseSelection.show(requireActivity().getSupportFragmentManager(), exerciseSelection.getTag());
            }
        });

        b1.setOnClickListener(v -> {
            dismiss();
            if (check){
                startActivity(new Intent(requireContext(), LevelExercisesActivity.class).putExtra("name", "B1"));
            } else {
                ExerciseSelection exerciseSelection = new ExerciseSelection("B1");
                exerciseSelection.show(requireActivity().getSupportFragmentManager(), exerciseSelection.getTag());
            }
        });

        return view;
    }
}
