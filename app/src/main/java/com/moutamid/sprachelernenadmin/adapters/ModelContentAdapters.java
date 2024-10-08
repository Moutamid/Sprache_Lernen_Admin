package com.moutamid.sprachelernenadmin.adapters;
import com.moutamid.sprachelernenadmin.Stash;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
 
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.activities.EditModelContentActivity;
import com.moutamid.sprachelernenadmin.models.VocabularyModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ModelContentAdapters extends RecyclerView.Adapter<ModelContentAdapters.ContentVH> implements Filterable {

    Context context;
    ArrayList<VocabularyModel> list;
    ArrayList<VocabularyModel> listAll;
    MediaPlayer mediaPlayer;
    int playingPosition = -1;
    private Handler handler = new Handler();

    public ModelContentAdapters(Context context, ArrayList<VocabularyModel> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        this.mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public ContentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentVH(LayoutInflater.from(context).inflate(R.layout.model_content, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContentVH holder, int position) {
        VocabularyModel model = list.get(holder.getAdapterPosition());

        holder.name.setText(model.getName());
        holder.german.setText(model.getNameGerman());

        holder.edit.setOnClickListener(v -> {
            Stash.put(Constants.PASS_CONTENT, model);
            context.startActivity(new Intent(context, EditModelContentActivity.class));
        });

        Glide.with(context).load(model.getImage()).into(holder.imageView);

        holder.pakistanFlag.setOnClickListener(v -> {
            holder.selectedFlag = true;
            holder.pakistanFlag.setStrokeColor(context.getResources().getColor(R.color.green));
            holder.germanFlag.setStrokeColor(context.getResources().getColor(R.color.grey));
        });
        holder.germanFlag.setOnClickListener(v -> {
            holder.selectedFlag = false;
            holder.pakistanFlag.setStrokeColor(context.getResources().getColor(R.color.grey));
            holder.germanFlag.setStrokeColor(context.getResources().getColor(R.color.green));
        });

        holder.play.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                if (playingPosition == position) {
                    playingPosition = -1;
                    return;
                }
//                holder.playIcon.setImageResource(R.drawable.round_play_arrow_24);
            }
//            holder.playIcon.setImageResource(R.drawable.round_pause_24);
            playingPosition = position;
            String audio = holder.selectedFlag ? model.getAudio() : model.getGermanAudio();
            playAudio(audio, holder.progress);
        });

        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(true)
                    .setTitle("Delete Topic Content")
                    .setMessage("Are You Sure you want to delete this?")
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.showDialog();
                        Constants.databaseReference().child(Constants.getLang()).child(Constants.VOCABULARY).child(Constants.CONTENT).child(model.getTopicID()).child(model.getID()).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(context, "Content Deleted Successfully", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .setNegativeButton("No", ((dialog, which) -> dialog.dismiss()))
                    .show();
        });

    }

    private void playAudio(String audioUrl, final LinearProgressIndicator progress) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                progress.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                updateProgress(progress);
            });

            mediaPlayer.setOnCompletionListener(mp -> progress.setProgress(0));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProgress(final LinearProgressIndicator progress) {
        if (mediaPlayer != null) {
            progress.setProgress(mediaPlayer.getCurrentPosition());
            if (mediaPlayer.isPlaying()) {
                handler.postDelayed(() -> updateProgress(progress), 100);
            } else {
                // Reset playing position when playback completes
                playingPosition = -1;
            }
        }
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<VocabularyModel> filterList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filterList.addAll(listAll);
            } else {
                for (VocabularyModel listModel : listAll) {
                    if (listModel.getName().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            listModel.getNameGerman().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filterList.add(listModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends VocabularyModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ContentVH extends RecyclerView.ViewHolder {
        TextView name, german;
        MaterialCardView delete, edit, play;
        LinearProgressIndicator progress;
        ImageView imageView, playIcon;
        MaterialCardView germanFlag, pakistanFlag;
        boolean selectedFlag;

        public ContentVH(@NonNull View itemView) {
            super(itemView);

            selectedFlag = true;

            name = itemView.findViewById(R.id.name);
            german = itemView.findViewById(R.id.german);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
            play = itemView.findViewById(R.id.play);
            playIcon = itemView.findViewById(R.id.playIcon);
            progress = itemView.findViewById(R.id.progress);
            imageView = itemView.findViewById(R.id.imageView);
            pakistanFlag = itemView.findViewById(R.id.pakistanFlag);
            germanFlag = itemView.findViewById(R.id.germanFlag);
        }
    }

}
