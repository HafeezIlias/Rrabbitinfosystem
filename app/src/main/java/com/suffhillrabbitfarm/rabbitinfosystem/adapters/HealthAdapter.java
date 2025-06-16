package com.suffhillrabbitfarm.rabbitinfosystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.models.HealthModel;
import com.suffhillrabbitfarm.rabbitinfosystem.models.MatingModel;

import java.util.List;

public class HealthAdapter extends RecyclerView.Adapter<HealthAdapter.CustomViewHolder> {
    List<HealthModel> hml;
    Context context;
    String operationType;
    private onItemClickListener mListener;

    // UPDATED: Interface with both delete and edit methods
    public interface onItemClickListener{
        void delete(int position);
        void edit(int position);  // ADDED: Edit method
    }

    public void setOnItemClickListener(onItemClickListener listener){
        mListener=listener;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView txtDate, txtPartner, txtNewBorn, txtDeath;
        TextView buttonDelete, buttonEdit;  // ADDED: Edit button

        public CustomViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtPartner = itemView.findViewById(R.id.txtPartner);
            txtNewBorn = itemView.findViewById(R.id.txtNewBorn);
            txtDeath = itemView.findViewById(R.id.txtDeath);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);  // ADDED: Find edit button

            // EXISTING: Delete button click listener
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.delete(getAdapterPosition());
                    }
                }
            });

            // ADDED: Edit button click listener
            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.edit(getAdapterPosition());
                    }
                }
            });
        }
    }

    public HealthAdapter(List<HealthModel> hml, Context context, String type) {
        this.hml = hml;
        this.context = context;
        this.operationType = type;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.health_item;
    }

    @Override
    public int getItemCount() {
        return hml.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        HealthModel hm = hml.get(position);

        // Set data to views (keeping your existing field mapping)
        holder.txtDate.setText(hm.getCurrentStatus() != null ? hm.getCurrentStatus() : "N/A");
        holder.txtPartner.setText(hm.getSlaughterDate() != null ? hm.getSlaughterDate() : "N/A");
        holder.txtNewBorn.setText(hm.getDeathDate() != null ? hm.getDeathDate() : "N/A");
        holder.txtDeath.setText(hm.getDeathCause() != null ? hm.getDeathCause() : "N/A");
    }
}