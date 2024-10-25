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
import com.moutamid.sprachelernenadmin.Stash;
import com.moutamid.sprachelernenadmin.activities.AddContentActivity;
import com.moutamid.sprachelernenadmin.models.ModelPaper;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import java.util.ArrayList;

public class PapersAdapter extends RecyclerView.Adapter<PapersAdapter.TopicVH> {
    Context context;
    ArrayList<ModelPaper> list;

    public PapersAdapter(Context context, ArrayList<ModelPaper> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TopicVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TopicVH(LayoutInflater.from(context).inflate(R.layout.papers, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopicVH holder, int position) {
        ModelPaper model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.name);

        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(true)
                    .setTitle("Delete " + model.name)
                    .setMessage("Are you sure you want to delete this paper.\nThe content of this model paper will also be deleted.")
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.databaseReference().child(Constants.getLang()).child(Constants.MODEL_PAPERS).child(model.id).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Paper Deleted Successfully", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .show();
        });

        holder.edit.setOnClickListener(v -> {
            Toast.makeText(context, "Edited", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TopicVH extends RecyclerView.ViewHolder {
        TextView name;
        MaterialCardView delete, edit;

        public TopicVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }

}
