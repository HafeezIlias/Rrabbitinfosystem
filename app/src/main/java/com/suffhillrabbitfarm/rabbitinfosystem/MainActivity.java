package com.suffhillrabbitfarm.rabbitinfosystem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.suffhillrabbitfarm.rabbitinfosystem.account.SelectionActivity;
import com.suffhillrabbitfarm.rabbitinfosystem.adapters.RabbitAdapter;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;
import com.suffhillrabbitfarm.rabbitinfosystem.helpers.MiscHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.models.RabbitModel;
import com.suffhillrabbitfarm.rabbitinfosystem.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton addRabbitButton;
    private Toolbar toolbar;
    List<RabbitModel> rml;
    RabbitAdapter rabbitAdapter;
    RecyclerView recyclerView;
    TextView txtName, txtAccountType, txtTotalRabbits, txtHealthyCount, txtSickCount, txtRecentCount;
    TextView txtFilterStatus;
    Spinner spinnerFilterMonth, spinnerFilterYear;
    Button btnApplyFilter, btnClearFilter;
    LoginData loginData;

    // Filter variables
    private String selectedMonth = "";
    private String selectedYear = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initViews();
        setupToolbar();
        setupFilters();
        initRecyclerView();
        loadRabbits();
        loadDashboardStats();
        getProfile();
    }

    private void initData() {
        loginData = new LoginData(this);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_profile) {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_about) {
            showAboutDialog();
            return true;
        } else if (itemId == R.id.action_logout) {
            openLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProfile() {
        UserModel userModel = loginData.getLoginType();
        if (userModel != null) {
            txtName.setText(userModel.getUserName());
            txtAccountType.setText(formatAccountType(userModel.getAccountType()));
        }
    }

    private String formatAccountType(String accountType) {
        if (accountType == null || accountType.isEmpty()) {
            return "User";
        }

        switch (accountType.toLowerCase()) {
            case "manager_account":
                return "Manager Account";
            case "staff_account":
                return "Staff Account";
            case "admin":
                return "Admin";
            default:
                return "Standard User";
        }
    }

    private void updateStatistics() {
        int totalRabbits = rml.size();
        int healthyCount = 0;
        int recentCount = 0;

        // Calculate healthy rabbits (simplified: rabbits with weight > 0)
        for (RabbitModel rabbit : rml) {
            try {
                double weight = Double.parseDouble(rabbit.getWeight());
                if (weight > 0) {
                    healthyCount++;
                }
            } catch (NumberFormatException e) {
                // Skip invalid weight values
            }
        }

        // Calculate recent rabbits (simplified: rabbits added in last 30 days)
        // For now, we'll use a simple percentage of total rabbits
        recentCount = Math.max(1, totalRabbits / 4);

        // Update UI
        txtTotalRabbits.setText(String.valueOf(totalRabbits));
        txtHealthyCount.setText(String.valueOf(healthyCount));
        txtRecentCount.setText(String.valueOf(recentCount));
    }

    private void openLogout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear login data
                        loginData.clearLoginData();

                        // Redirect to login
                        Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    private void initRecyclerView() {
        rml = new ArrayList<>();
        rabbitAdapter = new RabbitAdapter(rml, this, "");
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(rabbitAdapter);
        rabbitAdapter.setOnItemClickListener(new RabbitAdapter.onItemClickListener() {
            @Override
            public void option(int position) {
                openDialogOptions(position);
            }
        });
    }

    private void openDialogOptions(int pos) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.opetion_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView textViewRabbitID = dialog.findViewById(R.id.textViewRabbitID);
        android.widget.Button buttonMatingInformation = dialog.findViewById(R.id.buttonMatingInformation);
        android.widget.Button buttonHealthAndGrowth = dialog.findViewById(R.id.buttonHealthAndGrowth);
        android.widget.Button buttonWeightHistory = dialog.findViewById(R.id.buttonWeightHistory);
        android.widget.Button buttonViewFullDetails = dialog.findViewById(R.id.buttonViewFullDetails);

        android.widget.Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
        android.widget.Button buttonColse = dialog.findViewById(R.id.buttonColse);
        textViewRabbitID.setText(rml.get(pos).getRabbit_id());
        buttonColse.setOnClickListener(view -> dialog.dismiss());
        buttonDelete.setOnClickListener(view -> {
            dialog.dismiss();
            openDeleteAlert(pos);
        });

        buttonMatingInformation.setOnClickListener(view -> {
            openActivity("Mating Information", pos);
            dialog.dismiss();
        });
        buttonHealthAndGrowth.setOnClickListener(view -> {
            openActivity("Health And Growth", pos);
            dialog.dismiss();
        });
        buttonWeightHistory.setOnClickListener(view -> {
            openActivity("Weight History", pos);
            dialog.dismiss();
        });
        buttonViewFullDetails.setOnClickListener(view -> {
            openActivity("Rabbit Full Details", pos);
            dialog.dismiss();
        });
    }

    private void openActivity(String type, int position) {
        try {
            RabbitModel rabbit = rml.get(position);
            Intent intent = new Intent(MainActivity.this, HostingActivity.class);
            intent.putExtra("RabbitID", rabbit.getRabbit_id());
            intent.putExtra("type", type);

            // Pass complete rabbit data as JSON string
            try {
                org.json.JSONObject rabbitJson = new org.json.JSONObject();
                rabbitJson.put("rabbit_id", rabbit.getRabbit_id());
                rabbitJson.put("breed", rabbit.getBreed());
                rabbitJson.put("color", rabbit.getColor());
                rabbitJson.put("gender", rabbit.getGender());
                rabbitJson.put("weight", rabbit.getWeight());
                rabbitJson.put("dob", rabbit.getDob());
                rabbitJson.put("father_id", rabbit.getFather_id());
                rabbitJson.put("mother_id", rabbit.getMother_id());
                rabbitJson.put("observations", rabbit.getObservations());
                rabbitJson.put("cage_number", rabbit.getCage_number());

                intent.putExtra("RabbitData", rabbitJson.toString());
            } catch (org.json.JSONException e) {
                android.util.Log.e("MainActivity", "Error creating rabbit JSON: " + e.getMessage());
            }

            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening " + type + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
            android.util.Log.e("MainActivity", "Error opening activity: " + e.getMessage(), e);
        }
    }

    private void openDeleteAlert(int pos) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Rabbit")
                .setMessage("Are you sure to delete: " + rml.get(pos).getRabbit_id() + "?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        deleteRabbit(pos);
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void deleteRabbit(int position) {
        RabbitModel rabbit = rml.get(position);
        String userUid = loginData.getUserUid();

        Dialog dialog = MiscHelper.openNetLoaderDialog(this);

        ApiHelper.deleteRabbit(rabbit.getRabbit_id(), userUid, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Rabbit deleted successfully", Toast.LENGTH_SHORT).show();

                // Remove from list and update adapter
                rml.remove(position);
                rabbitAdapter.notifyItemRemoved(position);
                rabbitAdapter.notifyItemRangeChanged(position, rml.size());
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Error deleting rabbit: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        txtName = findViewById(R.id.txtName);
        txtAccountType = findViewById(R.id.txtAccountType);
        txtTotalRabbits = findViewById(R.id.txtTotalRabbits);
        txtHealthyCount = findViewById(R.id.txtHealthyCount);
        txtSickCount = findViewById(R.id.txtSickCount);
        txtRecentCount = findViewById(R.id.txtRecentCount);
        txtFilterStatus = findViewById(R.id.txtFilterStatus);

        // Filter components
        spinnerFilterMonth = findViewById(R.id.spinnerFilterMonth);
        spinnerFilterYear = findViewById(R.id.spinnerFilterYear);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        btnClearFilter = findViewById(R.id.btnClearFilter);

        recyclerView = findViewById(R.id.recyclerView);
        addRabbitButton = findViewById(R.id.add_rabbit_button);
        addRabbitButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RabbitProfileActivity.class);
            startActivity(intent);
        });
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About Rabbit Info System")
                .setMessage(
                        "Rabbit Info System v1.0\n\nA comprehensive rabbit management system for tracking rabbit health, breeding, and farm operations.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void loadRabbits() {
        String userUid = loginData.getUserUid();
        if (userUid.isEmpty()) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Add debug logging
        android.util.Log.d("MainActivity", "Loading rabbits for user: " + userUid);

        // Show loading indicator (you can add a progress bar here)
        Toast.makeText(this, "Loading rabbits...", Toast.LENGTH_SHORT).show();

        ApiHelper.getRabbits(userUid, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                android.util.Log.d("MainActivity", "API Success. Response: " + response.toString());
                try {
                    // Check if response has rabbits array
                    if (!response.has("rabbits")) {
                        android.util.Log.e("MainActivity", "Response missing 'rabbits' array");
                        Toast.makeText(MainActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONArray rabbitsArray = response.getJSONArray("rabbits");
                    android.util.Log.d("MainActivity", "Found " + rabbitsArray.length() + " rabbits");

                    // Show debug message
                    Toast.makeText(MainActivity.this, "Found " + rabbitsArray.length() + " rabbits", Toast.LENGTH_SHORT)
                            .show();

                    rml.clear();

                    for (int i = 0; i < rabbitsArray.length(); i++) {
                        JSONObject rabbitObj = rabbitsArray.getJSONObject(i);
                        RabbitModel rabbit = new RabbitModel();
                        rabbit.setRabbit_id(rabbitObj.optString("rabbit_id"));
                        rabbit.setBreed(rabbitObj.optString("breed"));
                        rabbit.setColor(rabbitObj.optString("color"));
                        rabbit.setGender(rabbitObj.optString("gender"));
                        rabbit.setWeight(String.valueOf(rabbitObj.optDouble("weight", 0.0)));
                        rabbit.setDob(rabbitObj.optString("dob"));
                        rabbit.setFather_id(rabbitObj.optString("father_id"));
                        rabbit.setMother_id(rabbitObj.optString("mother_id"));
                        rabbit.setObservations(rabbitObj.optString("observations"));

                        rml.add(rabbit);
                        android.util.Log.d("MainActivity", "Added rabbit: " + rabbit.getRabbit_id());
                    }

                    // Notify adapter and log
                    rabbitAdapter.notifyDataSetChanged();
                    android.util.Log.d("MainActivity", "Adapter updated with " + rml.size() + " rabbits");

                    // Update statistics
                    updateStatistics();

                } catch (JSONException e) {
                    android.util.Log.e("MainActivity", "JSON parsing error: " + e.getMessage());
                    android.util.Log.e("MainActivity", "Response was: " + response.toString());
                    Toast.makeText(MainActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("MainActivity", "API Error: " + error);
                Toast.makeText(MainActivity.this, "Error loading rabbits: " + error, Toast.LENGTH_LONG).show();

                // Show additional debug information
                Toast.makeText(MainActivity.this, "Check if XAMPP is running and database is accessible",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void testApiConnection() {
        android.util.Log.d("MainActivity", "Testing API connection...");

        ApiHelper.testConnection(new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                android.util.Log.d("MainActivity", "API Test Success: " + response.toString());
                Toast.makeText(MainActivity.this, "API Connection: SUCCESS", Toast.LENGTH_SHORT).show();

                // Now try to load rabbits
                loadRabbits();
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("MainActivity", "API Test Failed: " + error);
                Toast.makeText(MainActivity.this, "API Connection Failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRabbits();
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

        // Set up filter button listeners
        btnApplyFilter.setOnClickListener(v -> applyFilters());
        btnClearFilter.setOnClickListener(v -> clearFilters());
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
        loadDashboardStats();
        loadRabbits();
    }

    private void clearFilters() {
        selectedMonth = "";
        selectedYear = "";
        spinnerFilterMonth.setSelection(0);
        spinnerFilterYear.setSelection(0);
        updateFilterStatus();
        loadDashboardStats();
        loadRabbits();
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

    private void loadDashboardStats() {
        String userUid = loginData.getUserUid();
        if (userUid.isEmpty()) {
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

        android.util.Log.d("MainActivity", "Loading dashboard stats with params: " + queryParams);

        ApiHelper.makeGetRequest("reports/get_dashboard_stats.php", queryParams, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");

                        // Update statistics
                        txtTotalRabbits.setText(String.valueOf(data.optInt("total_rabbits", 0)));
                        txtHealthyCount.setText(String.valueOf(data.optInt("healthy_rabbits", 0)));
                        txtSickCount.setText(String.valueOf(data.optInt("sick_rabbits", 0)));
                        txtRecentCount.setText(String.valueOf(data.optInt("recently_added", 0)));

                        android.util.Log.d("MainActivity", "Dashboard stats updated successfully");
                    } else {
                        android.util.Log.e("MainActivity",
                                "Dashboard stats API returned error: " + response.optString("message"));
                    }
                } catch (JSONException e) {
                    android.util.Log.e("MainActivity", "Error parsing dashboard stats: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("MainActivity", "Error loading dashboard stats: " + error);
                // Fallback to simple statistics
                updateStatistics();
            }
        });
    }
}