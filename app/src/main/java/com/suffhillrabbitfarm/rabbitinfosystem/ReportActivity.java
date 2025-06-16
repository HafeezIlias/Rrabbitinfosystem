package com.suffhillrabbitfarm.rabbitinfosystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;

import org.json.JSONObject;

public class ReportActivity extends AppCompatActivity {

    private TextView textViewTotalRabbits, textViewHealthyRabbits, textViewSickRabbits;
    private Button buttonGenerateReport, buttonBack;
    private LoginData loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initData();
        initViews();
        loadReportData();
    }

    private void initData() {
        loginData = new LoginData(this);
    }

    private void initViews() {
        textViewTotalRabbits = findViewById(R.id.txtTotalRabbits);
        textViewHealthyRabbits = findViewById(R.id.txtMaleRabbits);
        textViewSickRabbits = findViewById(R.id.txtFemaleRabbits);
        buttonGenerateReport = findViewById(R.id.btnBackToMenu);
        buttonBack = findViewById(R.id.btnBackToMenu);

        buttonGenerateReport.setOnClickListener(view -> generateReport());
        buttonBack.setOnClickListener(view -> finish());
    }

    private void loadReportData() {
        String userUid = loginData.getUserUid();
        if (userUid.isEmpty()) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ApiHelper.getDashboardStats(userUid, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");

                    // Update UI with real data
                    textViewTotalRabbits.setText("Total Rabbits: " + data.optInt("total_rabbits", 0));
                    textViewHealthyRabbits.setText("Male: " + data.optInt("male_rabbits", 0));
                    textViewSickRabbits.setText("Female: " + data.optInt("female_rabbits", 0));

                    // Additional stats could be displayed in more TextViews
                    String topBreed = data.optString("top_breed", "None");
                    double avgWeight = data.optDouble("average_weight", 0.0);
                    int recentlyAdded = data.optInt("recently_added", 0);

                    Toast.makeText(ReportActivity.this,
                            String.format("Top breed: %s | Avg weight: %.1fkg | Recently added: %d",
                                    topBreed, avgWeight, recentlyAdded),
                            Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Toast.makeText(ReportActivity.this, "Error parsing dashboard data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                textViewTotalRabbits.setText("Total Rabbits: Error loading");
                textViewHealthyRabbits.setText("Male: Error loading");
                textViewSickRabbits.setText("Female: Error loading");
                Toast.makeText(ReportActivity.this, "Error loading statistics: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateReport() {
        Toast.makeText(this, "Full report generation will be implemented with additional API endpoints",
                Toast.LENGTH_LONG).show();
    }
}