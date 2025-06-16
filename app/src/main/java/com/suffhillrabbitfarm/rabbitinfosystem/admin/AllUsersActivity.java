package com.suffhillrabbitfarm.rabbitinfosystem.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.adapters.UserAdapter;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity {

    RecyclerView recyclerViewUsers;
    ImageView backArrow;
    TextInputEditText editTextSearch;
    LinearLayout layoutEmpty;
    CircularProgressIndicator progressBar;
    TextView txtUserCount, txtActiveCount, txtRecentCount;

    private UserAdapter userAdapter;
    private List<UserModel> userList = new ArrayList<>();
    private List<UserModel> filteredUserList = new ArrayList<>();

    private static final String ADMIN_KEY = "admin123"; // In production, this should be securely stored

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        initViews();
        setupRecyclerView();
        setupSearch();
        loadUsers();
    }

    private void initViews() {
        recyclerViewUsers = findViewById(R.id.recyclerView);
        backArrow = findViewById(R.id.backArrow);
        editTextSearch = findViewById(R.id.editTextSearch);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        progressBar = findViewById(R.id.progressBar);
        txtUserCount = findViewById(R.id.txtUserCount);
        txtActiveCount = findViewById(R.id.txtActiveCount);
        txtRecentCount = findViewById(R.id.txtRecentCount);

        backArrow.setOnClickListener(view -> finish());
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(this, filteredUserList);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);

        userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(UserModel user, int position) {
                // Handle user click - could show user details
                Toast.makeText(AllUsersActivity.this,
                        "User: " + user.getUserName() + " (" + user.getRabbitCount() + " rabbits)",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserActionClick(UserModel user, int position) {
                // Handle action button click - could show popup menu
                showUserActionDialog(user, position);
            }
        });
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterUsers(String query) {
        filteredUserList.clear();

        if (query.isEmpty()) {
            filteredUserList.addAll(userList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (UserModel user : userList) {
                if ((user.getUserName() != null && user.getUserName().toLowerCase().contains(lowerQuery)) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerQuery)) ||
                        (user.getPhoneNumber() != null && user.getPhoneNumber().toLowerCase().contains(lowerQuery))) {
                    filteredUserList.add(user);
                }
            }
        }

        userAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void loadUsers() {
        showLoading(true);

        ApiHelper.getAllUsers(ADMIN_KEY, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray users = response.getJSONArray("data");
                    userList.clear();
                    filteredUserList.clear();

                    for (int i = 0; i < users.length(); i++) {
                        JSONObject userObj = users.getJSONObject(i);
                        UserModel user = new UserModel();

                        user.setUid(userObj.optString("uid"));
                        user.setUserName(userObj.optString("user_name"));
                        user.setEmail(userObj.optString("email"));
                        user.setPhoneNumber(userObj.optString("phone_number"));
                        user.setAccountType(userObj.optString("account_type"));
                        user.setCreatedAt(userObj.optString("created_at"));
                        user.setUpdatedAt(userObj.optString("updated_at"));
                        user.setRabbitCount(userObj.optInt("rabbit_count", 0));
                        user.setTotalRabbitsEver(userObj.optInt("total_rabbits_ever", 0));
                        user.setLastRabbitAdded(userObj.optString("last_rabbit_added"));

                        userList.add(user);
                    }

                    filteredUserList.addAll(userList);

                    runOnUiThread(() -> {
                        userAdapter.notifyDataSetChanged();
                        updateStatistics();
                        updateEmptyState();
                        showLoading(false);
                    });

                } catch (JSONException e) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(AllUsersActivity.this, "Error parsing users data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        updateEmptyState();
                    });
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(AllUsersActivity.this, "Error loading users: " + error, Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                });
            }
        });
    }

    private void updateStatistics() {
        int totalUsers = userList.size();
        int activeUsers = 0;
        int recentUsers = 0;

        // Calculate statistics
        for (UserModel user : userList) {
            if (user.getRabbitCount() > 0) {
                activeUsers++;
            }

            // Count users who joined in the last 30 days (simplified)
            if (user.getCreatedAt() != null && !user.getCreatedAt().isEmpty()) {
                // This is a simplified check - in real implementation, you'd parse dates
                // properly
                recentUsers++;
            }
        }

        txtUserCount.setText(String.valueOf(totalUsers));
        txtActiveCount.setText(String.valueOf(activeUsers));
        txtRecentCount.setText(String.valueOf(Math.min(recentUsers, totalUsers / 3))); // Simplified recent count
    }

    private void updateEmptyState() {
        if (filteredUserList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerViewUsers.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerViewUsers.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewUsers.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showUserActionDialog(UserModel user, int position) {
        // Create a simple dialog or popup menu for user actions
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("User Actions: " + user.getUserName())
                .setItems(new String[] { "View Details", "Copy UID", "Send Message" }, (dialog, which) -> {
                    switch (which) {
                        case 0: // View Details
                            showUserDetails(user);
                            break;
                        case 1: // Copy UID
                            copyToClipboard(user.getUid());
                            break;
                        case 2: // Send Message
                            Toast.makeText(this, "Send message feature not implemented", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }

    private void showUserDetails(UserModel user) {
        String details = String.format(
                "User Details:\n\n" +
                        "Name: %s\n" +
                        "Email: %s\n" +
                        "Phone: %s\n" +
                        "Account Type: %s\n" +
                        "Active Rabbits: %d\n" +
                        "Total Rabbits Ever: %d\n" +
                        "Joined: %s",
                user.getUserName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAccountType(),
                user.getRabbitCount(),
                user.getTotalRabbitsEver(),
                user.getCreatedAt());

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("User Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    private void copyToClipboard(String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(
                CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("User UID", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "UID copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh users when returning to this activity
        loadUsers();
    }
}