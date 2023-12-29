package com.moutamid.sprachelernenadmin.bottomsheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.activities.AddContentActivity;
import com.moutamid.sprachelernenadmin.activities.AddExerciseActivity;
import com.moutamid.sprachelernenadmin.adapters.ExListAdapter;
import com.moutamid.sprachelernenadmin.adapters.ExerciseListAdapter;
import com.moutamid.sprachelernenadmin.adapters.TopicsListAdapter;
import com.moutamid.sprachelernenadmin.models.Exercise;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExerciseSelection extends BottomSheetDialogFragment {
    String level;
    ArrayList<Exercise> list;

    public ExerciseSelection(String level) {
        this.level = level;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exercise_selection, container, false);

        RecyclerView topicList = view.findViewById(R.id.topicList);
        MaterialCardView close = view.findViewById(R.id.close);
        LinearLayout progress = view.findViewById(R.id.progress);

        list = new ArrayList<>();

        close.setOnClickListener(v -> {
            dismiss();
        });

        topicList.setLayoutManager(new LinearLayoutManager(requireContext()));
        topicList.setHasFixedSize(false);

        Constants.databaseReference().child(Constants.getLang()).child(Constants.EXERCISE_LIST).child(level).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        list.clear();
                        for (DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                            Exercise topicsModel = dataSnapshot2.getValue(Exercise.class);
                            list.add(topicsModel);
                        }
                        if (list.size() > 0){
                            Collections.reverse(list);
                            topicList.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        } else {
                            topicList.setVisibility(View.GONE);
                            progress.setVisibility(View.VISIBLE);
                        }

                        Collections.sort(list, new Comparator<Exercise>() {
                            @Override
                            public int compare(Exercise o1, Exercise o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });

                        ExListAdapter adapter = new ExListAdapter(requireContext(), list, model -> {
                            dismiss();
                            startActivity(new Intent(requireContext(), AddExerciseActivity.class).putExtra(Constants.LEVEL, level).putExtra(Constants.exercise, model.getName()));
                        });
                        topicList.setAdapter(adapter);
                    } else {
                        Toast.makeText(requireContext(), "Nothing Found!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        return view;
    }
}
