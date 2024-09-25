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
import com.moutamid.sprachelernenadmin.activities.EditContentActivity;
import com.moutamid.sprachelernenadmin.activities.WritingEditActivity;
import com.moutamid.sprachelernenadmin.models.WritingModel;

import java.util.ArrayList;

public class WritingAdapter extends RecyclerView.Adapter<WritingAdapter.ContentVH> {
    Context context;
    ArrayList<WritingModel> list;

    public WritingAdapter(Context context, ArrayList<WritingModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ContentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentVH(LayoutInflater.from(context).inflate(R.layout.content, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContentVH holder, int position) {
        WritingModel model = list.get(holder.getAdapterPosition());
        holder.topic.setText("Writing");
        holder.heading.setText(model.getTopic());
        holder.note.setText(model.getLetter());

        holder.edit.setOnClickListener(v -> {
            context.startActivity(new Intent(context, WritingEditActivity.class).putExtra(Constants.ID, model.getId()));
        });

        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(true)
                    .setTitle("Delete Topic Content")
                    .setMessage("Are You Sure you want to delete this?")
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.showDialog();
                        Constants.databaseReference().child(Constants.getLang()).child(Constants.WRITING).child(model.getId()).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(context, "Topic Deleted Successfully", Toast.LENGTH_SHORT).show();
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
        TextView topic, heading, note;
        MaterialCardView delete, edit;

        public ContentVH(@NonNull View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.topic);
            heading = itemView.findViewById(R.id.heading);
            note = itemView.findViewById(R.id.note);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

}
