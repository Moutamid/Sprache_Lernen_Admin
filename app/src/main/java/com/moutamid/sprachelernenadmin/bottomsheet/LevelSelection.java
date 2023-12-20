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

public class LevelSelection extends BottomSheetDialogFragment {
    MaterialCardView a1,a2,b1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.level_selection, container, false);

        a1 = view.findViewById(R.id.A1);
        a2 = view.findViewById(R.id.A2);
        b1 = view.findViewById(R.id.B1);

        a1.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddExerciseActivity.class).putExtra(Constants.LEVEL, "A1")));
        a2.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddExerciseActivity.class).putExtra(Constants.LEVEL, "A2")));
        b1.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddExerciseActivity.class).putExtra(Constants.LEVEL, "B1")));

        return view;
    }
}
