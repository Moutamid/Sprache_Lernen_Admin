package com.moutamid.sprachelernenadmin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.sprachelernenadmin.Constants;
import com.moutamid.sprachelernenadmin.R;
import com.moutamid.sprachelernenadmin.models.PaymentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentVH> {
    Context context;
    ArrayList<PaymentModel> list;

    public PaymentAdapter(Context context, ArrayList<PaymentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PaymentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaymentVH(LayoutInflater.from(context).inflate(R.layout.payment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentVH holder, int position) {
        PaymentModel model = list.get(holder.getAdapterPosition());
        holder.duration.setText(model.getDuration());
        holder.money.setText(model.getPrice());
        holder.buyerName.setText(model.getUsername());
        holder.buyerEmail.setText(model.getUserEmail());

        String time = new SimpleDateFormat("dd-MM-yyyy | hh:mm aa", Locale.getDefault()).format(model.getTimestamp());
        holder.time.setText(time);

        holder.proof.setOnClickListener(v -> {
            showProof(model.getProofImage());
        });

        holder.approve.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Approve Payment")
                    .setMessage("Are you sure you want to approve this subscription?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        model.setApprove(true);
                        model.setTimestamp(System.currentTimeMillis());
                        Constants.databaseReference().child(Constants.PAYMENTS).child(model.getUserID()).child(model.getID()).setValue(model)
                                .addOnSuccessListener(unused -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("vip", true);
                                    map.put("subscriptionFromGoogle", false);
                                    map.put("duration", model.getDuration());
                                    Constants.databaseReference().child(Constants.USER).child(model.getUserID()).updateChildren(map)
                                            .addOnSuccessListener(unused1 -> Toast.makeText(context, "Subscription Approved", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        holder.delete.setOnClickListener(v -> {
            Constants.databaseReference().child(Constants.PAYMENTS).child(model.getUserID()).child(model.getID()).removeValue();
        });
    }

    private void showProof(String proofImage) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_preview);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);
        dialog.show();

        ImageView imageView = dialog.findViewById(R.id.proof);
        LinearLayout progress = dialog.findViewById(R.id.progress);
        new Handler().postDelayed(() -> {
            progress.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }, 3000);
        Glide.with(context).load(proofImage).into(imageView);
        MaterialCardView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PaymentVH extends RecyclerView.ViewHolder {
        TextView duration, time, money, buyerName, buyerEmail;
        MaterialCardView delete;
        MaterialButton proof, approve;

        public PaymentVH(@NonNull View itemView) {
            super(itemView);
            duration = itemView.findViewById(R.id.duration);
            time = itemView.findViewById(R.id.time);
            money = itemView.findViewById(R.id.money);
            buyerName = itemView.findViewById(R.id.buyerName);
            buyerEmail = itemView.findViewById(R.id.buyerEmail);
            delete = itemView.findViewById(R.id.delete);
            proof = itemView.findViewById(R.id.proof);
            approve = itemView.findViewById(R.id.approve);
        }
    }

}
