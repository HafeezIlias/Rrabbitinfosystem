package com.suffhillrabbitfarm.rabbitinfosystem.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatingFragment extends Fragment {

    EditText editTextMateId, editTextMatingDate, editTextNotes;
    Button buttonAddMating;
    RecyclerView recyclerViewMating;
    TextView textViewNoData;
    String rabbitId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mating, container, false);

        // Get rabbit ID from bundle arguments
        if (getArguments() != null) {
            rabbitId = getArguments().getString("RabbitID");
        }

        initViews(view);
        loadMatingRecords();

        return view;
    }

    private void initViews(View view) {
        // Note: The current layout only has basic views, so we'll use what's available
        // and show appropriate messages for missing functionality

        buttonAddMating = view.findViewById(R.id.buttonAdd);
        recyclerViewMating = view.findViewById(R.id.recyclerView);
        textViewNoData = view.findViewById(R.id.textViewTop);

        // Set the header text
        textViewNoData.setText("Mating Records for Rabbit: " + (rabbitId != null ? rabbitId : "Unknown"));

        buttonAddMating.setOnClickListener(v -> {
            // For now, just show a message since we don't have proper input fields
            Toast.makeText(getContext(), "Add mating feature will be implemented with proper form fields",
                    Toast.LENGTH_LONG).show();
        });

        loadMatingRecords();
    }

    private void addMatingRecord() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            Toast.makeText(getContext(), "Rabbit ID is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // For demo purposes, using dummy values since UI elements are limited
        String maleRabbitId = "M001"; // You would get this from a spinner or EditText
        String matingDate = "2024-01-15"; // You would get this from a date picker
        String expectedDeliveryDate = "2024-02-15"; // Calculated or selected
        int litterSize = 0;
        int liveBirths = 0;
        int stillbirths = 0;
        String notes = "First mating";
        String status = "planned";

        ApiHelper.addMatingRecord(rabbitId, maleRabbitId, matingDate, expectedDeliveryDate,
                litterSize, liveBirths, stillbirths, notes, status,
                new ApiHelper.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(getContext(), "Mating record added successfully", Toast.LENGTH_SHORT).show();
                        loadMatingRecords(); // Reload the list
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMatingRecords() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            textViewNoData.setVisibility(View.VISIBLE);
            recyclerViewMating.setVisibility(View.GONE);
            return;
        }

        ApiHelper.getMatingRecords(rabbitId, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray records = response.getJSONArray("data");
                    if (records.length() > 0) {
                        textViewNoData.setVisibility(View.GONE);
                        recyclerViewMating.setVisibility(View.VISIBLE);
                        // TODO: Create and set adapter for mating records
                        Toast.makeText(getContext(), "Found " + records.length() + " mating records",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        textViewNoData.setVisibility(View.VISIBLE);
                        recyclerViewMating.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                textViewNoData.setVisibility(View.VISIBLE);
                recyclerViewMating.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading records: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}