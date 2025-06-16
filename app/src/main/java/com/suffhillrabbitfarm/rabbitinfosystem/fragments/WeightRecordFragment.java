package com.suffhillrabbitfarm.rabbitinfosystem.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WeightRecordFragment extends Fragment {

    EditText editTextWeight, editTextNotes;
    Button buttonAddWeight;
    RecyclerView recyclerViewWeights;
    TextView textViewNoData;
    private String selectedDate;
    private String rabbitId;
    private LoginData loginData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight_record, container, false);

        // Get rabbit ID from arguments
        if (getArguments() != null) {
            rabbitId = getArguments().getString("rabbitId", "");
        }

        loginData = new LoginData(getContext());
        initViews(view);
        loadWeightRecords();

        return view;
    }

    private void initViews(View view) {
        // Note: Using available IDs from the layout - in a real implementation,
        // you'd update the layout to have proper input fields
        buttonAddWeight = view.findViewById(R.id.buttonAdd);
        recyclerViewWeights = view.findViewById(R.id.recyclerView);
        textViewNoData = view.findViewById(R.id.textViewTop);

        recyclerViewWeights.setLayoutManager(new LinearLayoutManager(getContext()));
        buttonAddWeight.setOnClickListener(v -> showAddWeightDialog());

        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(Calendar.getInstance().getTime());
    }

    private void showAddWeightDialog() {
        // Show date picker
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = year + "-" + String.format("%02d", month + 1) + "-"
                            + String.format("%02d", dayOfMonth);
                    addWeightRecord();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void addWeightRecord() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            Toast.makeText(getContext(), "Rabbit ID is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // For demo purposes, using dummy values since UI elements are limited
        // In a real implementation, you'd get these from EditText fields
        String weightStr = "2.5";
        String notes = "Regular checkup";

        try {
            Double weight = Double.parseDouble(weightStr);
            String userUid = loginData.getUserUid();

            ApiHelper.addWeightRecord(rabbitId, weight, selectedDate, notes, userUid,
                    new ApiHelper.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Toast.makeText(getContext(), "Weight record added successfully", Toast.LENGTH_SHORT).show();
                            loadWeightRecords(); // Reload the list
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid weight format", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadWeightRecords() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            textViewNoData.setVisibility(View.VISIBLE);
            recyclerViewWeights.setVisibility(View.GONE);
            return;
        }

        ApiHelper.getWeightRecords(rabbitId, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray records = response.getJSONArray("data");
                    if (records.length() > 0) {
                        textViewNoData.setVisibility(View.GONE);
                        recyclerViewWeights.setVisibility(View.VISIBLE);
                        // TODO: Create and set adapter for weight records
                        Toast.makeText(getContext(), "Found " + records.length() + " weight records",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        textViewNoData.setVisibility(View.VISIBLE);
                        recyclerViewWeights.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                textViewNoData.setVisibility(View.VISIBLE);
                recyclerViewWeights.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading records: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}