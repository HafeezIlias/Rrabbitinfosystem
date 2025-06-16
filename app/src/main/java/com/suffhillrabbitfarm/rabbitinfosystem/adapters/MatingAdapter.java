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
import com.suffhillrabbitfarm.rabbitinfosystem.models.MatingModel;
import com.suffhillrabbitfarm.rabbitinfosystem.models.RabbitModel;

import java.util.List;

public class MatingAdapter extends RecyclerView.Adapter<MatingAdapter.CustomViewHolder> {
    List<MatingModel> mml;
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

    public MatingAdapter(List<MatingModel> mml, Context context, String type) {
        this.mml = mml;
        this.context = context;
        this.operationType = type;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.mating_item;
    }

    @Override
    public int getItemCount() {
        return mml.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        MatingModel mm = mml.get(position);

        // Set data to views with null checks
        holder.txtDate.setText(mm.getDate() != null ? mm.getDate() : "N/A");
        holder.txtPartner.setText(mm.getPartner() != null ? mm.getPartner() : "N/A");
        holder.txtNewBorn.setText(mm.getNewBorns() != null ? mm.getNewBorns() : "0");
        holder.txtDeath.setText(mm.getDeaths() != null ? mm.getDeaths() : "0");
    }
}