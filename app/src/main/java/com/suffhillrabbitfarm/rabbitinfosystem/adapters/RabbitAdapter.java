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
import com.suffhillrabbitfarm.rabbitinfosystem.models.RabbitModel;
import java.util.List;

public class RabbitAdapter extends RecyclerView.Adapter<RabbitAdapter.CustomViewHolder> {
    List<RabbitModel> rml;
    Context context;
    String operationType;
    private onItemClickListener mListener;

    public interface onItemClickListener {
        void option(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {// item click listener initialization
        mListener = listener;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView rabbit_id_text, rabbit_breed, rabbit_age, rabbit_status, last_updated, textViewOptions;
        View health_indicator;

        public CustomViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            rabbit_id_text = itemView.findViewById(R.id.rabbit_id_text);
            rabbit_breed = itemView.findViewById(R.id.rabbit_breed);
            rabbit_age = itemView.findViewById(R.id.rabbit_age);
            rabbit_status = itemView.findViewById(R.id.rabbit_status);
            last_updated = itemView.findViewById(R.id.last_updated);
            health_indicator = itemView.findViewById(R.id.health_indicator);
            textViewOptions = itemView.findViewById(R.id.textViewOptions);
            textViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.option(getAdapterPosition());
                    }
                }
            });

        }
    }

    public RabbitAdapter(List<RabbitModel> rml, Context context, String type) {
        this.rml = rml;
        this.context = context;
        this.operationType = type;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.rabbit_list_item;
    }

    @Override
    public int getItemCount() {
        return rml.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false),
                mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        RabbitModel rm = rml.get(position);

        // Set rabbit ID
        holder.rabbit_id_text.setText(rm.getRabbit_id());

        // Set breed
        holder.rabbit_breed
                .setText(rm.getBreed() != null && !rm.getBreed().isEmpty() ? rm.getBreed() : "Unknown Breed");

        // Set age (simplified for now)
        holder.rabbit_age.setText("Adult");

        // Set status
        holder.rabbit_status.setText("Healthy");

        // Set last updated
        holder.last_updated.setText("Recently");
    }

}
