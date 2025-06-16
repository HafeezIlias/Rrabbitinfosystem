package com.suffhillrabbitfarm.rabbitinfosystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.models.HealthRecordModel;

import java.util.List;

public class HealthRecordAdapter extends RecyclerView.Adapter<HealthRecordAdapter.HealthRecordViewHolder> {

    private List<HealthRecordModel> healthRecords;

    public HealthRecordAdapter(List<HealthRecordModel> healthRecords) {
        this.healthRecords = healthRecords;
    }

    @NonNull
    @Override
    public HealthRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_record, parent, false);
        return new HealthRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthRecordViewHolder holder, int position) {
        HealthRecordModel record = healthRecords.get(position);

        // Set health status
        String healthStatus = record.getHealth_status() != null ? record.getHealth_status() : "Unknown";
        holder.textViewHealthStatus.setText(healthStatus);

        // Set record date
        holder.textViewRecordDate.setText(record.getRecord_date());

        // Set symptoms (show only if not empty)
        if (record.getSymptoms() != null && !record.getSymptoms().trim().isEmpty()) {
            holder.textViewSymptoms.setText("Symptoms: " + record.getSymptoms());
            holder.textViewSymptoms.setVisibility(View.VISIBLE);
        } else {
            holder.textViewSymptoms.setVisibility(View.GONE);
        }

        // Set diagnosis (show only if not empty)
        if (record.getDiagnosis() != null && !record.getDiagnosis().trim().isEmpty()) {
            holder.textViewDiagnosis.setText("Diagnosis: " + record.getDiagnosis());
            holder.textViewDiagnosis.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDiagnosis.setVisibility(View.GONE);
        }

        // Set treatment (show only if not empty)
        if (record.getTreatment() != null && !record.getTreatment().trim().isEmpty()) {
            holder.textViewTreatment.setText("Treatment: " + record.getTreatment());
            holder.textViewTreatment.setVisibility(View.VISIBLE);
        } else {
            holder.textViewTreatment.setVisibility(View.GONE);
        }

        // Show vet information if available
        boolean hasVetInfo = (record.getVet_name() != null && !record.getVet_name().trim().isEmpty()) ||
                (record.getCost() != null && !record.getCost().trim().isEmpty() && !record.getCost().equals("0"));

        if (hasVetInfo) {
            holder.layoutVetInfo.setVisibility(View.VISIBLE);

            String vetName = "Vet: " + (record.getVet_name() != null && !record.getVet_name().trim().isEmpty()
                    ? record.getVet_name()
                    : "Not specified");
            holder.textViewVetName.setText(vetName);

            if (record.getCost() != null && !record.getCost().trim().isEmpty() && !record.getCost().equals("0")) {
                String cost = "Cost: $" + record.getCost();
                holder.textViewCost.setText(cost);
                holder.textViewCost.setVisibility(View.VISIBLE);
            } else {
                holder.textViewCost.setVisibility(View.GONE);
            }
        } else {
            holder.layoutVetInfo.setVisibility(View.GONE);
        }

        // Set notes (show only if not empty)
        if (record.getNotes() != null && !record.getNotes().trim().isEmpty()) {
            holder.textViewNotes.setText("Notes: " + record.getNotes());
            holder.textViewNotes.setVisibility(View.VISIBLE);
        } else {
            holder.textViewNotes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return healthRecords.size();
    }

    public void updateData(List<HealthRecordModel> newRecords) {
        this.healthRecords = newRecords;
        notifyDataSetChanged();
    }

    static class HealthRecordViewHolder extends RecyclerView.ViewHolder {
        TextView textViewHealthStatus, textViewRecordDate, textViewSymptoms, textViewDiagnosis;
        TextView textViewTreatment, textViewVetName, textViewCost, textViewNotes;
        LinearLayout layoutVetInfo;

        public HealthRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHealthStatus = itemView.findViewById(R.id.textViewHealthStatus);
            textViewRecordDate = itemView.findViewById(R.id.textViewRecordDate);
            textViewSymptoms = itemView.findViewById(R.id.textViewSymptoms);
            textViewDiagnosis = itemView.findViewById(R.id.textViewDiagnosis);
            textViewTreatment = itemView.findViewById(R.id.textViewTreatment);
            textViewVetName = itemView.findViewById(R.id.textViewVetName);
            textViewCost = itemView.findViewById(R.id.textViewCost);
            textViewNotes = itemView.findViewById(R.id.textViewNotes);
            layoutVetInfo = itemView.findViewById(R.id.layoutVetInfo);
        }
    }
}