package com.moutamid.sprachelernenadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import org.w3c.dom.Text;

import java.util.ArrayList;

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

        holder.delete.setOnClickListener(v -> {
            Constants.showDialog();
            String name = Stash.getString(Constants.SELECT, Constants.URDU);
            Constants.databaseReference().child(name).child(Constants.TOPICS).child(model.getID()).removeValue()
                    .addOnSuccessListener(unused -> {
                        Constants.dismissDialog();
                        Toast.makeText(context, "Topic Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TopicVH extends RecyclerView.ViewHolder {
        TextView name;
        MaterialCardView delete;

        public TopicVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text);
            delete = itemView.findViewById(R.id.delete);
        }
    }

}
