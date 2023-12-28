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
import com.moutamid.sprachelernenadmin.activities.AddContentActivity;
import com.moutamid.sprachelernenadmin.activities.AddExerciseActivity;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.adapters.TopicsListAdapter;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import java.util.ArrayList;
import java.util.Collections;

public class ContentTopicList extends BottomSheetDialogFragment {

    ArrayList<TopicsModel> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topics_list, container, false);

        RecyclerView topicList = view.findViewById(R.id.topicList);
        MaterialCardView close = view.findViewById(R.id.close);
        LinearLayout progress = view.findViewById(R.id.progress);

        list = new ArrayList<>();

        close.setOnClickListener(v -> {
            dismiss();
        });

        topicList.setLayoutManager(new LinearLayoutManager(requireContext()));
        topicList.setHasFixedSize(false);

        Constants.databaseReference().child(Constants.getLang()).child(Constants.TOPICS).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){
                                TopicsModel topicsModel = dataSnapshot2.getValue(TopicsModel.class);
                                list.add(topicsModel);
                            }
                        }
                        if (list.size() > 0){
                            Collections.reverse(list);
                            topicList.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        } else {
                            topicList.setVisibility(View.GONE);
                            progress.setVisibility(View.VISIBLE);
                        }
                        TopicsListAdapter adapter = new TopicsListAdapter(requireContext(), list, model -> {
                            dismiss();
                            Stash.put(Constants.PASS, model);
                            startActivity(new Intent(requireContext(), AddContentActivity.class));
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
