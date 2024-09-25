package com.moutamid.sprachelernenadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

 
import com.google.android.material.card.MaterialCardView;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.clickListners.ClickListner;
import com.moutamid.sprachelernenadmin.models.TopicsModel;

import java.util.ArrayList;

public class TopicsListAdapter extends RecyclerView.Adapter<TopicsListAdapter.TopicVH> {
    Context context;
    ArrayList<TopicsModel> list;
    ClickListner clickListner;

    public TopicsListAdapter(Context context, ArrayList<TopicsModel> list, ClickListner clickListner) {
        this.context = context;
        this.list = list;
        this.clickListner = clickListner;
    }

    @NonNull
    @Override
    public TopicVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TopicVH(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopicVH holder, int position) {
        TopicsModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.getTopicName());
        holder.content.setText(model.getContentType());
        holder.itemView.setOnClickListener(v -> clickListner.onClick(list.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TopicVH extends RecyclerView.ViewHolder {
        TextView name;
        TextView content;
        public TopicVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text);
            content = itemView.findViewById(R.id.content);
        }
    }

}
