package com.suffhillrabbitfarm.rabbitinfosystem.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.models.MatingModel;
import com.suffhillrabbitfarm.rabbitinfosystem.models.WeightModel;

import java.util.List;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.CustomViewHolder> {
    List<WeightModel> wml;
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
        TextView txtDate, txtWeight;
        TextView buttonDelete, buttonEdit;  // ADDED: Edit button

        public CustomViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtWeight = itemView.findViewById(R.id.txtWeight);
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

    public WeightAdapter(List<WeightModel> wml, Context context, String type) {
        this.wml = wml;
        this.context = context;
        this.operationType = type;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.weight_item;
    }

    @Override
    public int getItemCount() {
        return wml.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        WeightModel wm = wml.get(position);

        // Set data to views with null checks
        holder.txtDate.setText(wm.getDate() != null ? wm.getDate() : "N/A");
        holder.txtWeight.setText(wm.getWeight() != null ? wm.getWeight() + " kg" : "N/A");
    }
}