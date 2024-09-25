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
import com.moutamid.sprachelernenadmin.activities.AddContentActivity;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import com.moutamid.sprachelernenadmin.Stash;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicVH> {
    Context context;
    ArrayList<TopicsModel> list;

    public TopicsAdapter(Context context, ArrayList<TopicsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TopicVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TopicVH(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopicVH holder, int position) {
        TopicsModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.getTopicName());
        holder.content.setText(model.getContentType());

        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(true)
                    .setTitle("Delete " + model.getTopicName())
                    .setMessage("Are you sure you want to delete this topic.\nThe content of this topic will also be deleted.")
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.showDialog();
                        Constants.databaseReference().child(Constants.getLang()).child(Constants.TOPICS).child(model.getContentType()).child(model.getID()).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(context, "Topic Deleted Successfully", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .show();
        });

        holder.itemView.setOnClickListener(v -> {
            Stash.put(Constants.PASS, model);
            context.startActivity(new Intent(context, AddContentActivity.class));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TopicVH extends RecyclerView.ViewHolder {
        TextView name;
        TextView content;
        MaterialCardView delete;

        public TopicVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text);
            content = itemView.findViewById(R.id.content);
            delete = itemView.findViewById(R.id.delete);
        }
    }

}
