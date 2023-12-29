package com.moutamid.sprachelernenadmin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.activities.ModelContentActivity;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import java.util.ArrayList;

public class ModelTopicsAdapter extends RecyclerView.Adapter<ModelTopicsAdapter.ModelTopicsVH> {

    Context context;
    ArrayList<TopicsModel> list;

    public ModelTopicsAdapter(Context context, ArrayList<TopicsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ModelTopicsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ModelTopicsVH(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ModelTopicsVH holder, int position) {
        TopicsModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.getTopicName());
        holder.content.setText(model.getContentType());

        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ModelContentActivity.class).putExtra(Constants.TOPICS, model.getID()));
        });

        holder.itemView.setOnLongClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.add_topic_model);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();

            TextInputLayout topic = dialog.findViewById(R.id.topic);
            Button complete = dialog.findViewById(R.id.complete);
            topic.getEditText().setText(model.getTopicName());
            complete.setOnClickListener(v1 -> {
                String topicName = topic.getEditText().getText().toString();
                if (!topicName.isEmpty()) {
                    dialog.dismiss();
                    Constants.showDialog();
                    model.setTopicName(topicName);
                    Constants.databaseReference().child(Constants.getLang()).child(Constants.VOCABULARY).child(Constants.TOPICS).child(model.getID()).setValue(model)
                            .addOnSuccessListener(unused -> {
                                Constants.dismissDialog();
                                Toast.makeText(context, "Topic Updated Successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    topic.setErrorEnabled(true);
                    topic.setError("Topic name is empty");
                }
            });

            return false;
        });

        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(true)
                    .setTitle("Delete " + model.getTopicName())
                    .setMessage("Are you sure you want to delete this topic.\nThe content of this topic will also be deleted.")
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.showDialog();

                        Constants.databaseReference().child(Constants.getLang()).child(Constants.VOCABULARY).child(Constants.TOPICS).child(model.getID()).removeValue()
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ModelTopicsVH extends RecyclerView.ViewHolder {
        TextView name;
        TextView content;
        MaterialCardView delete;

        public ModelTopicsVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text);
            content = itemView.findViewById(R.id.content);
            delete = itemView.findViewById(R.id.delete);
        }
    }

}
