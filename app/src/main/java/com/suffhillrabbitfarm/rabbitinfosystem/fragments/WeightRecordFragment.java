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
import com.suffhillrabbitfarm.rabbitinfosystem.adapters.WeightRecordAdapter;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;
import com.suffhillrabbitfarm.rabbitinfosystem.models.WeightRecordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeightRecordFragment extends Fragment {

    EditText editTextWeight, editTextDate, editTextNotes;
    Button buttonAddWeight;
    RecyclerView recyclerViewWeights;
    TextView textViewNoData;

    private String rabbitId;
    private LoginData loginData;
    private WeightRecordAdapter adapter;
    private List<WeightRecordModel> weightRecords;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight_record, container, false);

        // Get rabbit ID from arguments
        if (getArguments() != null) {
            rabbitId = getArguments().getString("RabbitID");
        }

        loginData = new LoginData(getContext());
        weightRecords = new ArrayList<>();

        initViews(view);
        setupRecyclerView();
        loadWeightRecords();

        return view;
    }

    private void initViews(View view) {
        editTextWeight = view.findViewById(R.id.editTextWeight);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextNotes = view.findViewById(R.id.editTextNotes);
        buttonAddWeight = view.findViewById(R.id.buttonAdd);
        recyclerViewWeights = view.findViewById(R.id.recyclerView);
        textViewNoData = view.findViewById(R.id.textViewNoData);

        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        editTextDate.setText(currentDate);

        // Set up date picker
        editTextDate.setOnClickListener(v -> showDatePicker());

        // Set up add button
        buttonAddWeight.setOnClickListener(v -> addWeightRecord());
    }

    private void setupRecyclerView() {
        adapter = new WeightRecordAdapter(weightRecords);
        recyclerViewWeights.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewWeights.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + String.format("%02d", month + 1) + "-"
                            + String.format("%02d", dayOfMonth);
                    editTextDate.setText(selectedDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void addWeightRecord() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            Toast.makeText(getContext(), "Rabbit ID is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String weightStr = editTextWeight.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        if (weightStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter weight", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty()) {
            Toast.makeText(getContext(), "Please select date", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Double weight = Double.parseDouble(weightStr);
            String userUid = loginData.getUserUid();

            ApiHelper.addWeightRecord(rabbitId, weight, date, notes, userUid,
                    new ApiHelper.ApiCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Toast.makeText(getContext(), "Weight record added successfully", Toast.LENGTH_SHORT).show();

                            // Clear form
                            editTextWeight.setText("");
                            editTextNotes.setText("");

                            // Reload records
                            loadWeightRecords();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid weight", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadWeightRecords() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            showNoData();
            return;
        }

        ApiHelper.getWeightRecords(rabbitId, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray records = response.getJSONArray("weight_records");
                    weightRecords.clear();

                    for (int i = 0; i < records.length(); i++) {
                        JSONObject recordObj = records.getJSONObject(i);
                        WeightRecordModel record = new WeightRecordModel();

                        record.setId(recordObj.optString("id"));
                        record.setRabbit_id(recordObj.optString("rabbit_id"));
                        record.setWeight(recordObj.optString("weight"));
                        record.setDate(recordObj.optString("date"));
                        record.setNotes(recordObj.optString("notes"));
                        record.setRecorded_by(recordObj.optString("recorded_by"));
                        record.setCreated_at(recordObj.optString("created_at"));

                        weightRecords.add(record);
                    }

                    if (weightRecords.size() > 0) {
                        showRecords();
                        adapter.updateData(weightRecords);
                    } else {
                        showNoData();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    showNoData();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading records: " + error, Toast.LENGTH_SHORT).show();
                showNoData();
            }
        });
    }

    private void showRecords() {
        textViewNoData.setVisibility(View.GONE);
        recyclerViewWeights.setVisibility(View.VISIBLE);
    }

    private void showNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
        recyclerViewWeights.setVisibility(View.GONE);
    }
}