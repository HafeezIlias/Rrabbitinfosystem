package com.suffhillrabbitfarm.rabbitinfosystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.models.MatingRecordModel;

import java.util.List;

public class MatingRecordAdapter extends RecyclerView.Adapter<MatingRecordAdapter.MatingRecordViewHolder> {

    private List<MatingRecordModel> matingRecords;

    public MatingRecordAdapter(List<MatingRecordModel> matingRecords) {
        this.matingRecords = matingRecords;
    }

    @NonNull
    @Override
    public MatingRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mating_record, parent, false);
        return new MatingRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatingRecordViewHolder holder, int position) {
        MatingRecordModel record = matingRecords.get(position);

        // Set male rabbit ID
        String maleText = "Male: " + (record.getMale_rabbit_id() != null ? record.getMale_rabbit_id() : "Unknown");
        holder.textViewMaleRabbitId.setText(maleText);

        // Set status
        String status = record.getStatus() != null ? record.getStatus() : "Pending";
        holder.textViewStatus.setText(status);

        // Set mating date
        String matingDate = "Mated: " + (record.getMating_date() != null ? record.getMating_date() : "Unknown");
        holder.textViewMatingDate.setText(matingDate);

        // Set expected delivery date
        String expectedDate = "Expected: "
                + (record.getExpected_delivery_date() != null ? record.getExpected_delivery_date() : "Unknown");
        holder.textViewExpectedDate.setText(expectedDate);

        // Show litter information if available
        if (record.getLitter_size() != null && !record.getLitter_size().isEmpty()
                && !record.getLitter_size().equals("0")) {
            holder.layoutLitterInfo.setVisibility(View.VISIBLE);

            String litterSize = "Litter: " + record.getLitter_size();
            holder.textViewLitterSize.setText(litterSize);

            String liveBirths = "Live: " + (record.getLive_births() != null ? record.getLive_births() : "0");
            holder.textViewLiveBirths.setText(liveBirths);

            String stillbirths = "Still: " + (record.getStillbirths() != null ? record.getStillbirths() : "0");
            holder.textViewStillbirths.setText(stillbirths);
        } else {
            holder.layoutLitterInfo.setVisibility(View.GONE);
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
        return matingRecords.size();
    }

    public void updateData(List<MatingRecordModel> newRecords) {
        this.matingRecords = newRecords;
        notifyDataSetChanged();
    }

    static class MatingRecordViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMaleRabbitId, textViewStatus, textViewMatingDate, textViewExpectedDate;
        TextView textViewLitterSize, textViewLiveBirths, textViewStillbirths, textViewNotes;
        LinearLayout layoutLitterInfo;

        public MatingRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMaleRabbitId = itemView.findViewById(R.id.textViewMaleRabbitId);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewMatingDate = itemView.findViewById(R.id.textViewMatingDate);
            textViewExpectedDate = itemView.findViewById(R.id.textViewExpectedDate);
            textViewLitterSize = itemView.findViewById(R.id.textViewLitterSize);
            textViewLiveBirths = itemView.findViewById(R.id.textViewLiveBirths);
            textViewStillbirths = itemView.findViewById(R.id.textViewStillbirths);
            textViewNotes = itemView.findViewById(R.id.textViewNotes);
            layoutLitterInfo = itemView.findViewById(R.id.layoutLitterInfo);
        }
    }
}