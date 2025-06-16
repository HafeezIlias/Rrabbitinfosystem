package com.suffhillrabbitfarm.rabbitinfosystem;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    // Health Statistics TextViews
    private TextView txtTotalRabbits, txtHealthyRabbits, txtSickRabbits;

    // Gender Statistics TextViews
    private TextView txtMaleRabbits, txtFemaleRabbits;

    // Additional Statistics TextViews
    private TextView txtTopBreed, txtAverageWeight, txtRecentlyAdded, txtHealthPercentage;

    // Filter Components
    private TextView txtFilterStatus;
    private Spinner spinnerFilterMonth, spinnerFilterYear;
    private Button btnApplyFilter, btnRefresh, btnBackToMenu;

    private LoginData loginData;

    // Filter variables
    private String selectedMonth = "";
    private String selectedYear = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initData();
        initViews();
        setupFilters();
        loadReportData();
    }

    private void initData() {
        loginData = new LoginData(this);
    }

    private void initViews() {
        // Health Statistics
        txtTotalRabbits = findViewById(R.id.txtTotalRabbits);
        txtHealthyRabbits = findViewById(R.id.txtHealthyRabbits);
        txtSickRabbits = findViewById(R.id.txtSickRabbits);

        // Gender Statistics
        txtMaleRabbits = findViewById(R.id.txtMaleRabbits);
        txtFemaleRabbits = findViewById(R.id.txtFemaleRabbits);

        // Additional Statistics
        txtTopBreed = findViewById(R.id.txtTopBreed);
        txtAverageWeight = findViewById(R.id.txtAverageWeight);
        txtRecentlyAdded = findViewById(R.id.txtRecentlyAdded);
        txtHealthPercentage = findViewById(R.id.txtHealthPercentage);

        // Filter Components
        txtFilterStatus = findViewById(R.id.txtFilterStatus);
        spinnerFilterMonth = findViewById(R.id.spinnerFilterMonth);
        spinnerFilterYear = findViewById(R.id.spinnerFilterYear);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);

        // Set button listeners
        btnApplyFilter.setOnClickListener(v -> applyFilters());
        btnRefresh.setOnClickListener(v -> loadReportData());
        btnBackToMenu.setOnClickListener(v -> finish());
    }

    private void setupFilters() {
        // Setup month spinner
        String[] months = { "All Months", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December" };
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterMonth.setAdapter(monthAdapter);

        // Setup year spinner
        List<String> years = new ArrayList<>();
        years.add("All Years");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 5; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterYear.setAdapter(yearAdapter);
    }

    private void applyFilters() {
        // Get selected values
        String monthSelection = spinnerFilterMonth.getSelectedItem().toString();
        String yearSelection = spinnerFilterYear.getSelectedItem().toString();

        // Convert month name to number
        selectedMonth = "";
        if (!monthSelection.equals("All Months")) {
            String[] months = { "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December" };
            for (int i = 0; i < months.length; i++) {
                if (months[i].equals(monthSelection)) {
                    selectedMonth = String.valueOf(i + 1);
                    break;
                }
            }
        }

        // Set year
        selectedYear = yearSelection.equals("All Years") ? "" : yearSelection;

        // Update filter status
        updateFilterStatus();

        // Reload data with filters
        loadReportData();
    }

    private void updateFilterStatus() {
        String status = "Showing all data";
        if (!selectedMonth.isEmpty() || !selectedYear.isEmpty()) {
            StringBuilder sb = new StringBuilder("Filtered by: ");
            if (!selectedYear.isEmpty()) {
                sb.append("Year ").append(selectedYear);
            }
            if (!selectedMonth.isEmpty()) {
                if (!selectedYear.isEmpty())
                    sb.append(", ");
                String[] months = { "January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December" };
                sb.append("Month ").append(months[Integer.parseInt(selectedMonth) - 1]);
            }
            status = sb.toString();
        }
        txtFilterStatus.setText(status);
    }

    private void loadReportData() {
        String userUid = loginData.getUserUid();
        if (userUid.isEmpty()) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Build query parameters with filters
        String queryParams = "user_uid=" + userUid;
        if (!selectedMonth.isEmpty()) {
            queryParams += "&month=" + selectedMonth;
        }
        if (!selectedYear.isEmpty()) {
            queryParams += "&year=" + selectedYear;
        }

        android.util.Log.d("ReportActivity", "Loading dashboard stats with params: " + queryParams);

        ApiHelper.makeGetRequest("reports/get_dashboard_stats.php", queryParams, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");

                        // Update Health Statistics
                        txtTotalRabbits.setText(String.valueOf(data.optInt("total_rabbits", 0)));
                        txtHealthyRabbits.setText(String.valueOf(data.optInt("healthy_rabbits", 0)));
                        txtSickRabbits.setText(String.valueOf(data.optInt("sick_rabbits", 0)));

                        // Update Gender Statistics
                        txtMaleRabbits.setText(String.valueOf(data.optInt("male_rabbits", 0)));
                        txtFemaleRabbits.setText(String.valueOf(data.optInt("female_rabbits", 0)));

                        // Update Additional Statistics
                        txtTopBreed.setText(data.optString("top_breed", "None"));
                        txtAverageWeight.setText(String.format("%.1f kg", data.optDouble("average_weight", 0.0)));
                        txtRecentlyAdded.setText(String.valueOf(data.optInt("recently_added", 0)));

                        // Update Health Percentage
                        if (data.has("health_percentage")) {
                            JSONObject healthPerc = data.getJSONObject("health_percentage");
                            String healthText = String.format("Healthy: %.1f%% | Sick: %.1f%% | Unknown: %.1f%%",
                                    healthPerc.optDouble("healthy", 0.0),
                                    healthPerc.optDouble("sick", 0.0),
                                    healthPerc.optDouble("unknown", 0.0));
                            txtHealthPercentage.setText(healthText);
                        } else {
                            txtHealthPercentage.setText("Health data not available");
                        }

                        android.util.Log.d("ReportActivity", "Dashboard stats updated successfully");
                        Toast.makeText(ReportActivity.this, "Report data loaded successfully", Toast.LENGTH_SHORT)
                                .show();

                    } else {
                        String errorMsg = response.optString("message", "Unknown error");
                        android.util.Log.e("ReportActivity", "Dashboard stats API returned error: " + errorMsg);
                        showErrorState("API Error: " + errorMsg);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ReportActivity", "Error parsing dashboard stats: " + e.getMessage());
                    showErrorState("Error parsing data: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("ReportActivity", "Error loading dashboard stats: " + error);
                showErrorState("Error loading data: " + error);
            }
        });
    }

    private void showErrorState(String error) {
        // Set error messages for all fields
        txtTotalRabbits.setText("Error");
        txtHealthyRabbits.setText("Error");
        txtSickRabbits.setText("Error");
        txtMaleRabbits.setText("Error");
        txtFemaleRabbits.setText("Error");
        txtTopBreed.setText("Error loading");
        txtAverageWeight.setText("Error loading");
        txtRecentlyAdded.setText("Error");
        txtHealthPercentage.setText("Error loading health data");

        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}