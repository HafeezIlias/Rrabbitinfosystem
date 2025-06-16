package com.suffhillrabbitfarm.rabbitinfosystem.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HealthAndGrowthFragment extends Fragment {

    EditText editTextHealthDate, editTextHealthNotes, editTextTreatment;
    Spinner spinnerHealthStatus;
    Button buttonAddHealth;
    RecyclerView recyclerViewHealth;
    TextView textViewNoData;
    String rabbitId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_and_growth, container, false);

        // Get rabbit ID from bundle arguments
        if (getArguments() != null) {
            rabbitId = getArguments().getString("RabbitID");
        }

        initViews(view);
        loadHealthRecords();

        return view;
    }

    private void initViews(View view) {
        // Note: The current layout only has basic views, so we'll use what's available
        // and show appropriate messages for missing functionality

        buttonAddHealth = view.findViewById(R.id.buttonAdd);
        recyclerViewHealth = view.findViewById(R.id.recyclerView);
        textViewNoData = view.findViewById(R.id.textViewTop);

        // Set the header text
        textViewNoData.setText("Health Records for Rabbit: " + (rabbitId != null ? rabbitId : "Unknown"));

        buttonAddHealth.setOnClickListener(v -> {
            // For now, just show a message since we don't have proper input fields
            Toast.makeText(getContext(), "Add health record feature will be implemented with proper form fields",
                    Toast.LENGTH_LONG).show();
        });

        loadHealthRecords();
    }

    private void addHealthRecord() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            Toast.makeText(getContext(), "Rabbit ID is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // For demo purposes, using dummy values since UI elements are limited
        String recordDate = "2024-01-20";
        String healthStatus = "healthy";
        String symptoms = "None";
        String diagnosis = "Routine checkup";
        String treatment = "None required";
        String medication = "";
        String dosage = "";
        String vetName = "Dr. Smith";
        String vetContact = "123-456-7890";
        Double cost = 50.0;
        String notes = "Healthy condition";

        ApiHelper.addHealthRecord(rabbitId, recordDate, healthStatus, symptoms, diagnosis,
                treatment, medication, dosage, vetName, vetContact, cost, notes,
                new ApiHelper.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(getContext(), "Health record added successfully", Toast.LENGTH_SHORT).show();
                        loadHealthRecords(); // Reload the list
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadHealthRecords() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            textViewNoData.setVisibility(View.VISIBLE);
            recyclerViewHealth.setVisibility(View.GONE);
            return;
        }

        ApiHelper.getHealthRecords(rabbitId, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray records = response.getJSONArray("data");
                    if (records.length() > 0) {
                        textViewNoData.setVisibility(View.GONE);
                        recyclerViewHealth.setVisibility(View.VISIBLE);
                        // TODO: Create and set adapter for health records
                        Toast.makeText(getContext(), "Found " + records.length() + " health records",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        textViewNoData.setVisibility(View.VISIBLE);
                        recyclerViewHealth.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                textViewNoData.setVisibility(View.VISIBLE);
                recyclerViewHealth.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading records: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}