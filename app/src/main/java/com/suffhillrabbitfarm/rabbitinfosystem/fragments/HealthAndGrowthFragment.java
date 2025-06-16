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
import com.suffhillrabbitfarm.rabbitinfosystem.adapters.HealthRecordAdapter;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;
import com.suffhillrabbitfarm.rabbitinfosystem.models.HealthRecordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HealthAndGrowthFragment extends Fragment {

    EditText editTextRecordDate, editTextHealthStatus, editTextSymptoms, editTextDiagnosis;
    EditText editTextTreatment, editTextVetName, editTextNotes;
    Button buttonAddHealth;
    RecyclerView recyclerViewHealth;
    TextView textViewNoData;

    private String rabbitId;
    private LoginData loginData;
    private HealthRecordAdapter adapter;
    private List<HealthRecordModel> healthRecords;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_and_growth, container, false);

        // Get rabbit ID from arguments
        if (getArguments() != null) {
            rabbitId = getArguments().getString("RabbitID");
        }

        loginData = new LoginData(getContext());
        healthRecords = new ArrayList<>();

        initViews(view);
        setupRecyclerView();
        loadHealthRecords();

        return view;
    }

    private void initViews(View view) {
        editTextRecordDate = view.findViewById(R.id.editTextRecordDate);
        editTextHealthStatus = view.findViewById(R.id.editTextHealthStatus);
        editTextSymptoms = view.findViewById(R.id.editTextSymptoms);
        editTextDiagnosis = view.findViewById(R.id.editTextDiagnosis);
        editTextTreatment = view.findViewById(R.id.editTextTreatment);
        editTextVetName = view.findViewById(R.id.editTextVetName);
        editTextNotes = view.findViewById(R.id.editTextNotes);
        buttonAddHealth = view.findViewById(R.id.buttonAdd);
        recyclerViewHealth = view.findViewById(R.id.recyclerView);
        textViewNoData = view.findViewById(R.id.textViewNoData);

        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        editTextRecordDate.setText(currentDate);

        // Set up date picker
        editTextRecordDate.setOnClickListener(v -> showDatePicker());

        // Set up add button
        buttonAddHealth.setOnClickListener(v -> addHealthRecord());
    }

    private void setupRecyclerView() {
        adapter = new HealthRecordAdapter(healthRecords);
        recyclerViewHealth.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewHealth.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + String.format("%02d", month + 1) + "-"
                            + String.format("%02d", dayOfMonth);
                    editTextRecordDate.setText(selectedDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void addHealthRecord() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            Toast.makeText(getContext(), "Rabbit ID is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String recordDate = editTextRecordDate.getText().toString().trim();
        String healthStatus = editTextHealthStatus.getText().toString().trim();
        String symptoms = editTextSymptoms.getText().toString().trim();
        String diagnosis = editTextDiagnosis.getText().toString().trim();
        String treatment = editTextTreatment.getText().toString().trim();
        String vetName = editTextVetName.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        if (recordDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select record date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (healthStatus.isEmpty()) {
            Toast.makeText(getContext(), "Please enter health status", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add health record with comprehensive information
        ApiHelper.addHealthRecord(rabbitId, recordDate, healthStatus, symptoms, diagnosis,
                treatment, "", "", vetName, "", 0.0, notes,
                new ApiHelper.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(getContext(), "Health record added successfully", Toast.LENGTH_SHORT).show();

                        // Clear form
                        editTextHealthStatus.setText("");
                        editTextSymptoms.setText("");
                        editTextDiagnosis.setText("");
                        editTextTreatment.setText("");
                        editTextVetName.setText("");
                        editTextNotes.setText("");

                        // Reload records
                        loadHealthRecords();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadHealthRecords() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            showNoData();
            return;
        }

        ApiHelper.getHealthRecords(rabbitId, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray records = response.getJSONArray("health_records");
                    healthRecords.clear();

                    for (int i = 0; i < records.length(); i++) {
                        JSONObject recordObj = records.getJSONObject(i);
                        HealthRecordModel record = new HealthRecordModel();

                        record.setId(recordObj.optString("id"));
                        record.setRabbit_id(recordObj.optString("rabbit_id"));
                        record.setRecord_date(recordObj.optString("record_date"));
                        record.setHealth_status(recordObj.optString("health_status"));
                        record.setSymptoms(recordObj.optString("symptoms"));
                        record.setDiagnosis(recordObj.optString("diagnosis"));
                        record.setTreatment(recordObj.optString("treatment"));
                        record.setMedication(recordObj.optString("medication"));
                        record.setDosage(recordObj.optString("dosage"));
                        record.setVet_name(recordObj.optString("vet_name"));
                        record.setVet_contact(recordObj.optString("vet_contact"));
                        record.setCost(recordObj.optString("cost"));
                        record.setNotes(recordObj.optString("notes"));
                        record.setCreated_at(recordObj.optString("created_at"));

                        healthRecords.add(record);
                    }

                    if (healthRecords.size() > 0) {
                        showRecords();
                        adapter.updateData(healthRecords);
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
        recyclerViewHealth.setVisibility(View.VISIBLE);
    }

    private void showNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
        recyclerViewHealth.setVisibility(View.GONE);
    }
}