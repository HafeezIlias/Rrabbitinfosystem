package com.suffhillrabbitfarm.rabbitinfosystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.models.WeightRecordModel;

import java.util.List;

public class WeightRecordAdapter extends RecyclerView.Adapter<WeightRecordAdapter.WeightRecordViewHolder> {

    private List<WeightRecordModel> weightRecords;

    public WeightRecordAdapter(List<WeightRecordModel> weightRecords) {
        this.weightRecords = weightRecords;
    }

    @NonNull
    @Override
    public WeightRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weight_record, parent, false);
        return new WeightRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightRecordViewHolder holder, int position) {
        WeightRecordModel record = weightRecords.get(position);

        // Format weight
        String weightText = record.getWeight() + " kg";
        holder.textViewWeight.setText(weightText);

        // Set date
        holder.textViewDate.setText(record.getDate());

        // Set notes (show only if not empty)
        if (record.getNotes() != null && !record.getNotes().trim().isEmpty()) {
            holder.textViewNotes.setText(record.getNotes());
            holder.textViewNotes.setVisibility(View.VISIBLE);
        } else {
            holder.textViewNotes.setVisibility(View.GONE);
        }

        // Set recorded by
        String recordedBy = "Recorded by: " + (record.getRecorded_by() != null && !record.getRecorded_by().isEmpty()
                ? record.getRecorded_by()
                : "Unknown");
        holder.textViewRecordedBy.setText(recordedBy);
    }

    @Override
    public int getItemCount() {
        return weightRecords.size();
    }

    public void updateData(List<WeightRecordModel> newRecords) {
        this.weightRecords = newRecords;
        notifyDataSetChanged();
    }

    static class WeightRecordViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWeight, textViewDate, textViewNotes, textViewRecordedBy;

        public WeightRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWeight = itemView.findViewById(R.id.textViewWeight);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewNotes = itemView.findViewById(R.id.textViewNotes);
            textViewRecordedBy = itemView.findViewById(R.id.textViewRecordedBy);
        }
    }
}