package com.suffhillrabbitfarm.rabbitinfosystem.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminMain extends AppCompatActivity {

    CardView cardViewAllUsers, cardViewSettings;
    TextView textViewKey;
    ImageView backArrow;
    TextView txtTotalUsers, txtActiveUsers, txtTotalRabbits;

    private static final String ADMIN_KEY = "admin123"; // In production, this should be securely stored

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        initViews();
        loadAdminStats();
    }

    private void initViews() {
        cardViewAllUsers = findViewById(R.id.cardViewAllUsers);
        cardViewSettings = findViewById(R.id.cardViewSettings);
        textViewKey = findViewById(R.id.textViewKey);
        backArrow = findViewById(R.id.backArrow);

        // Statistics TextViews
        txtTotalUsers = findViewById(R.id.txtTotalUsers);
        txtActiveUsers = findViewById(R.id.txtActiveUsers);
        txtTotalRabbits = findViewById(R.id.txtTotalRabbits);

        // Set click listeners
        cardViewAllUsers.setOnClickListener(view -> {
            startActivity(new Intent(AdminMain.this, AllUsersActivity.class));
        });

        cardViewSettings.setOnClickListener(view -> {
            startActivity(new Intent(AdminMain.this, ChangeKeyActivity.class));
        });

        textViewKey.setOnClickListener(view -> {
            startActivity(new Intent(AdminMain.this, ChangeKeyActivity.class));
        });

        backArrow.setOnClickListener(view -> finish());
    }

    private void loadAdminStats() {
        ApiHelper.getAllUsers(ADMIN_KEY, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray users = response.getJSONArray("data");

                    final int totalUsers = users.length();
                    int activeUsersCount = 0;
                    int totalRabbitsCount = 0;

                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);

                        // Count active users (users with rabbits)
                        int rabbitCount = user.optInt("rabbit_count", 0);
                        if (rabbitCount > 0) {
                            activeUsersCount++;
                        }

                        // Sum total rabbits
                        totalRabbitsCount += user.optInt("total_rabbits_ever", 0);
                    }

                    final int activeUsers = activeUsersCount;
                    final int totalRabbits = totalRabbitsCount;

                    // Update UI on main thread
                    runOnUiThread(() -> {
                        txtTotalUsers.setText(String.valueOf(totalUsers));
                        txtActiveUsers.setText(String.valueOf(activeUsers));
                        txtTotalRabbits.setText(String.valueOf(totalRabbits));
                    });

                } catch (JSONException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AdminMain.this, "Error parsing admin data", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminMain.this, "Error loading admin stats: " + error, Toast.LENGTH_SHORT).show();
                    // Set default values
                    txtTotalUsers.setText("0");
                    txtActiveUsers.setText("0");
                    txtTotalRabbits.setText("0");
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh stats when returning to this activity
        loadAdminStats();
    }
}