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
import com.suffhillrabbitfarm.rabbitinfosystem.adapters.MatingRecordAdapter;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;
import com.suffhillrabbitfarm.rabbitinfosystem.models.MatingRecordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MatingFragment extends Fragment {

    EditText editTextMaleRabbitId, editTextMatingDate, editTextExpectedDelivery, editTextNotes;
    Button buttonAddMating;
    RecyclerView recyclerViewMating;
    TextView textViewNoData;

    private String rabbitId;
    private LoginData loginData;
    private MatingRecordAdapter adapter;
    private List<MatingRecordModel> matingRecords;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mating, container, false);

        // Get rabbit ID from arguments
        if (getArguments() != null) {
            rabbitId = getArguments().getString("RabbitID");
        }

        loginData = new LoginData(getContext());
        matingRecords = new ArrayList<>();

        initViews(view);
        setupRecyclerView();
        loadMatingRecords();

        return view;
    }

    private void initViews(View view) {
        editTextMaleRabbitId = view.findViewById(R.id.editTextMaleRabbitId);
        editTextMatingDate = view.findViewById(R.id.editTextMatingDate);
        editTextExpectedDelivery = view.findViewById(R.id.editTextExpectedDelivery);
        editTextNotes = view.findViewById(R.id.editTextNotes);
        buttonAddMating = view.findViewById(R.id.buttonAdd);
        recyclerViewMating = view.findViewById(R.id.recyclerView);
        textViewNoData = view.findViewById(R.id.textViewNoData);

        // Set current date for mating date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        editTextMatingDate.setText(currentDate);

        // Calculate expected delivery date (31 days from mating date)
        Calendar expectedCal = Calendar.getInstance();
        expectedCal.add(Calendar.DAY_OF_MONTH, 31);
        String expectedDate = sdf.format(expectedCal.getTime());
        editTextExpectedDelivery.setText(expectedDate);

        // Set up date pickers
        editTextMatingDate.setOnClickListener(v -> showMatingDatePicker());
        editTextExpectedDelivery.setOnClickListener(v -> showExpectedDatePicker());

        // Set up add button
        buttonAddMating.setOnClickListener(v -> addMatingRecord());
    }

    private void setupRecyclerView() {
        adapter = new MatingRecordAdapter(matingRecords);
        recyclerViewMating.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMating.setAdapter(adapter);
    }

    private void showMatingDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + String.format("%02d", month + 1) + "-"
                            + String.format("%02d", dayOfMonth);
                    editTextMatingDate.setText(selectedDate);

                    // Auto-calculate expected delivery date (31 days later)
                    Calendar expectedCal = Calendar.getInstance();
                    expectedCal.set(year, month, dayOfMonth);
                    expectedCal.add(Calendar.DAY_OF_MONTH, 31);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String expectedDate = sdf.format(expectedCal.getTime());
                    editTextExpectedDelivery.setText(expectedDate);

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showExpectedDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + String.format("%02d", month + 1) + "-"
                            + String.format("%02d", dayOfMonth);
                    editTextExpectedDelivery.setText(selectedDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void addMatingRecord() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            Toast.makeText(getContext(), "Rabbit ID is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String maleRabbitId = editTextMaleRabbitId.getText().toString().trim();
        String matingDate = editTextMatingDate.getText().toString().trim();
        String expectedDeliveryDate = editTextExpectedDelivery.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        if (maleRabbitId.isEmpty()) {
            Toast.makeText(getContext(), "Please enter male rabbit ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (matingDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select mating date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expectedDeliveryDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select expected delivery date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add mating record with default values for litter information
        ApiHelper.addMatingRecord(rabbitId, maleRabbitId, matingDate, expectedDeliveryDate,
                0, 0, 0, notes, "Pending",
                new ApiHelper.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(getContext(), "Mating record added successfully", Toast.LENGTH_SHORT).show();

                        // Clear form
                        editTextMaleRabbitId.setText("");
                        editTextNotes.setText("");

                        // Reload records
                        loadMatingRecords();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMatingRecords() {
        if (rabbitId == null || rabbitId.isEmpty()) {
            showNoData();
            return;
        }

        ApiHelper.getMatingRecords(rabbitId, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray records = response.getJSONArray("mating_records");
                    matingRecords.clear();

                    for (int i = 0; i < records.length(); i++) {
                        JSONObject recordObj = records.getJSONObject(i);
                        MatingRecordModel record = new MatingRecordModel();

                        record.setId(recordObj.optString("id"));
                        record.setRabbit_id(recordObj.optString("rabbit_id"));
                        record.setMale_rabbit_id(recordObj.optString("male_rabbit_id"));
                        record.setMating_date(recordObj.optString("mating_date"));
                        record.setExpected_delivery_date(recordObj.optString("expected_delivery_date"));
                        record.setLitter_size(recordObj.optString("litter_size"));
                        record.setLive_births(recordObj.optString("live_births"));
                        record.setStillbirths(recordObj.optString("stillbirths"));
                        record.setNotes(recordObj.optString("notes"));
                        record.setStatus(recordObj.optString("status"));
                        record.setCreated_at(recordObj.optString("created_at"));

                        matingRecords.add(record);
                    }

                    if (matingRecords.size() > 0) {
                        showRecords();
                        adapter.updateData(matingRecords);
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
        recyclerViewMating.setVisibility(View.VISIBLE);
    }

    private void showNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
        recyclerViewMating.setVisibility(View.GONE);
    }
}