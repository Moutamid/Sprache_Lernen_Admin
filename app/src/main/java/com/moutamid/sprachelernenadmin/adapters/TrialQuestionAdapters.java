package com.moutamid.sprachelernenadmin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

 
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.activities.EditExerciseActivity;
import com.moutamid.sprachelernenadmin.activities.TrialEditActivity;
import com.moutamid.sprachelernenadmin.models.ExerciseModel;

import java.util.ArrayList;
import com.moutamid.sprachelernenadmin.Stash;

public class TrialQuestionAdapters extends RecyclerView.Adapter<TrialQuestionAdapters.ContentVH> {

    Context context;
    ArrayList<ExerciseModel> list;

    public TrialQuestionAdapters(Context context, ArrayList<ExerciseModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ContentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentVH(LayoutInflater.from(context).inflate(R.layout.exercise, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContentVH holder, int position) {
        ExerciseModel model = list.get(holder.getAdapterPosition());

        holder.topic.setText(model.getLevel());
        holder.question.setText(model.getQuestion());
        String ans = "";

        for (String s : model.getOptions()){
            ans += s + ", ";
        }

        holder.answers.setText(ans);

        holder.edit.setOnClickListener(v -> {
            Stash.put(Constants.PASS_EXERCISE, model);
            context.startActivity(new Intent(context, TrialEditActivity.class));
        });

        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(true)
                    .setTitle("Delete Exercise")
                    .setMessage("Are You Sure you want to delete this?")
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.showDialog();
                        Constants.databaseReference().child(Constants.TRIAL_QUESTIONS).child(model.getID()).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(context, "Exercise Deleted Successfully", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .setNegativeButton("No", ((dialog, which) -> dialog.dismiss()))
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ContentVH extends RecyclerView.ViewHolder {
        TextView topic, question, answers;
        MaterialCardView delete, edit;

        public ContentVH(@NonNull View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.topic);
            question = itemView.findViewById(R.id.question);
            answers = itemView.findViewById(R.id.answers);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

}
