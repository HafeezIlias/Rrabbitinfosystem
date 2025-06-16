package com.suffhillrabbitfarm.rabbitinfosystem.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiHelper {

    // =========================== CONFIGURATION ===========================

    // Debug tag for logging
    private static final String DEBUG_TAG = "ApiHelper";

    // *** SINGLE BASE URL CONFIGURATION ***
    // Update this with your server URL - Change only this line for different
    // environments:

    // For Android Emulator (default)
    private static final String BASE_URL = "http://10.0.2.2/Rrabbitinfosystem/php_api/";

    // For Real Device - Replace with your computer's IP address
    // To find your IP: Windows: ipconfig | Mac/Linux: ifconfig
    // private static final String BASE_URL =
    // "http://192.168.1.100/Rrabbitinfosystem/php_api/";

    // For Production Server
    // private static final String BASE_URL = "https://yourdomain.com/api/";

    // Account type constants
    public static final String STAFF_ACCOUNT = "staff_account";
    public static final String MANAGER_ACCOUNT = "manager_account";
    public static final String ADMIN_ACCOUNT = "admin";

    // API Endpoints - All endpoints in one place
    public static final String LOGIN_ENDPOINT = "auth/login.php";
    public static final String REGISTER_ENDPOINT = "auth/register.php";
    public static final String PASSWORD_RESET_ENDPOINT = "auth/password_reset.php";
    public static final String VERIFY_RESET_TOKEN_ENDPOINT = "auth/verify_reset_token.php";

    public static final String GET_RABBITS_ENDPOINT = "rabbits/list.php";
    public static final String ADD_RABBIT_ENDPOINT = "rabbits/add.php";
    public static final String DELETE_RABBIT_ENDPOINT = "rabbits/delete.php";
    public static final String GET_RABBIT_BREEDS_ENDPOINT = "rabbits/get_breeds.php";

    public static final String GET_WEIGHT_RECORDS_ENDPOINT = "rabbits/get_weight_records.php";
    public static final String ADD_WEIGHT_RECORD_ENDPOINT = "rabbits/add_weight_record.php";

    public static final String GET_HEALTH_RECORDS_ENDPOINT = "health/get_health_records.php";
    public static final String ADD_HEALTH_RECORD_ENDPOINT = "health/add_health_record.php";

    public static final String GET_MATING_RECORDS_ENDPOINT = "mating/get_mating_records.php";
    public static final String ADD_MATING_RECORD_ENDPOINT = "mating/add_mating_record.php";

    public static final String ADD_EXPENSE_ENDPOINT = "financial/add_expense.php";
    public static final String ADD_SALE_ENDPOINT = "financial/add_sale.php";

    public static final String GET_DASHBOARD_STATS_ENDPOINT = "reports/get_dashboard_stats.php";
    public static final String GET_ALL_USERS_ENDPOINT = "admin/get_all_users.php";
    public static final String CHANGE_ADMIN_KEY_ENDPOINT = "admin/change_admin_key.php";

    // =========================== CORE CONNECTION LOGIC ===========================

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ApiCallback {
        void onSuccess(JSONObject response);

        void onError(String error);
    }

    public interface ApiArrayCallback {
        void onSuccess(JSONArray response);

        void onError(String error);
    }

    // Helper method to get full URL
    public static String getFullUrl(String endpoint) {
        return BASE_URL + endpoint;
    }

    // Helper method to check connection status
    public static void testConnection(ApiCallback callback) {
        makeGetRequest("test_connection.php", null, callback);
    }

    // Generic POST request method
    public static void makePostRequest(String endpoint, JSONObject requestData, ApiCallback callback) {
        executor.execute(() -> {
            String fullUrl = getFullUrl(endpoint);
            Log.d(DEBUG_TAG, "=== API POST REQUEST DEBUG ===");
            Log.d(DEBUG_TAG, "URL: " + fullUrl);
            Log.d(DEBUG_TAG, "Request Data: " + requestData.toString());

            try {
                URL url = new URL(fullUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                Log.d(DEBUG_TAG, "Connection established, sending data...");

                // Send request data
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = requestData.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Read response
                int responseCode = conn.getResponseCode();
                String responseMessage = conn.getResponseMessage();
                Log.d(DEBUG_TAG, "Response Code: " + responseCode + " (" + responseMessage + ")");

                StringBuilder response = new StringBuilder();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }

                String responseStr = response.toString();
                Log.d(DEBUG_TAG, "Raw Response: " + responseStr);
                Log.d(DEBUG_TAG, "Response Length: " + responseStr.length());

                // Parse JSON response
                try {
                    JSONObject jsonResponse = new JSONObject(responseStr);
                    Log.d(DEBUG_TAG, "✓ JSON parsing successful");

                    mainHandler.post(() -> {
                        if (jsonResponse.optBoolean("success", false)) {
                            Log.d(DEBUG_TAG, "✓ API call successful");
                            callback.onSuccess(jsonResponse);
                        } else {
                            String errorMsg = jsonResponse.optString("message", "Unknown error");
                            Log.d(DEBUG_TAG, "✗ API returned error: " + errorMsg);
                            callback.onError(errorMsg);
                        }
                    });
                } catch (JSONException jsonE) {
                    Log.e(DEBUG_TAG, "✗ JSON parsing failed: " + jsonE.getMessage());
                    Log.e(DEBUG_TAG, "Server response was: " + responseStr);

                    // Create user-friendly error message
                    String debugInfo = String.format(
                            "DEBUG INFO:\n" +
                                    "URL: %s\n" +
                                    "Response Code: %d\n" +
                                    "Response: %s",
                            fullUrl, responseCode,
                            responseStr.length() > 300 ? responseStr.substring(0, 300) + "..." : responseStr);

                    mainHandler.post(() -> callback.onError("Server returned invalid response:\n" + debugInfo));
                }

            } catch (IOException e) {
                Log.e(DEBUG_TAG, "✗ Network error: " + e.getMessage());
                mainHandler.post(() -> callback.onError("Network connection failed:\n" +
                        "URL: " + fullUrl + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "Check if XAMPP is running and URL is correct"));
            }
        });
    }

    // Generic GET request method
    public static void makeGetRequest(String endpoint, String queryParams, ApiCallback callback) {
        executor.execute(() -> {
            String fullUrl = getFullUrl(endpoint);
            if (queryParams != null && !queryParams.isEmpty()) {
                fullUrl += "?" + queryParams;
            }

            Log.d(DEBUG_TAG, "=== API GET REQUEST DEBUG ===");
            Log.d(DEBUG_TAG, "URL: " + fullUrl);

            try {
                URL url = new URL(fullUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Read response
                int responseCode = conn.getResponseCode();
                String responseMessage = conn.getResponseMessage();
                Log.d(DEBUG_TAG, "Response Code: " + responseCode + " (" + responseMessage + ")");

                StringBuilder response = new StringBuilder();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }

                String responseStr = response.toString();
                Log.d(DEBUG_TAG, "Raw Response: " + responseStr);
                Log.d(DEBUG_TAG, "Response Length: " + responseStr.length());

                // Parse JSON response
                try {
                    JSONObject jsonResponse = new JSONObject(responseStr);
                    Log.d(DEBUG_TAG, "✓ JSON parsing successful");

                    mainHandler.post(() -> {
                        if (jsonResponse.optBoolean("success", false)) {
                            Log.d(DEBUG_TAG, "✓ API call successful");
                            callback.onSuccess(jsonResponse);
                        } else {
                            String errorMsg = jsonResponse.optString("message", "Unknown error");
                            Log.d(DEBUG_TAG, "✗ API returned error: " + errorMsg);
                            callback.onError(errorMsg);
                        }
                    });
                } catch (JSONException jsonE) {
                    Log.e(DEBUG_TAG, "✗ JSON parsing failed: " + jsonE.getMessage());
                    Log.e(DEBUG_TAG, "Server response was: " + responseStr);

                    // Create user-friendly error message
                    String debugInfo = String.format(
                            "DEBUG INFO:\n" +
                                    "URL: %s\n" +
                                    "Response Code: %d\n" +
                                    "Response: %s",
                            fullUrl, responseCode,
                            responseStr.length() > 300 ? responseStr.substring(0, 300) + "..." : responseStr);

                    mainHandler.post(() -> callback.onError("Invalid JSON response. Server returned:\n" + debugInfo));
                }

            } catch (IOException e) {
                Log.e(DEBUG_TAG, "✗ Network error: " + e.getMessage());
                String finalFullUrl = fullUrl;
                mainHandler.post(() -> callback.onError("Network connection failed:\n" +
                        "URL: " + finalFullUrl + "\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "Check if XAMPP is running and URL is correct"));
            }
        });
    }

    // Authentication methods
    public static void login(String email, String password, ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("email", email);
            requestData.put("password", password);
            makePostRequest(LOGIN_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    public static void register(String email, String password, String userName, String phoneNumber, String accountType,
            ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("email", email);
            requestData.put("password", password);
            requestData.put("user_name", userName);
            requestData.put("phone_number", phoneNumber);
            requestData.put("account_type", accountType);
            makePostRequest(REGISTER_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    public static void requestPasswordReset(String email, ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("email", email);
            makePostRequest(PASSWORD_RESET_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    public static void verifyResetToken(String token, String newPassword, ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("token", token);
            requestData.put("new_password", newPassword);
            makePostRequest(VERIFY_RESET_TOKEN_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    // Rabbit management methods
    public static void getRabbits(String userUid, ApiCallback callback) {
        makeGetRequest(GET_RABBITS_ENDPOINT, "user_uid=" + userUid, callback);
    }

    public static void getRabbitBreeds(ApiCallback callback) {
        makeGetRequest(GET_RABBIT_BREEDS_ENDPOINT, "", callback);
    }

    public static void addRabbit(String userUid, String rabbitId, String breed, String color, String gender,
            Double weight, String dob, String fatherId, String motherId, String observations, ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("user_uid", userUid);
            requestData.put("rabbit_id", rabbitId);
            requestData.put("breed", breed);
            requestData.put("color", color);
            requestData.put("gender", gender);
            requestData.put("weight", weight);
            requestData.put("dob", dob);
            requestData.put("father_id", fatherId);
            requestData.put("mother_id", motherId);
            requestData.put("observations", observations);
            makePostRequest(ADD_RABBIT_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    public static void deleteRabbit(String rabbitId, String userUid, ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("rabbit_id", rabbitId);
            requestData.put("user_uid", userUid);
            makePostRequest(DELETE_RABBIT_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    // Weight records methods
    public static void getWeightRecords(String rabbitId, ApiCallback callback) {
        makeGetRequest(GET_WEIGHT_RECORDS_ENDPOINT, "rabbit_id=" + rabbitId, callback);
    }

    public static void addWeightRecord(String rabbitId, Double weight, String date, String notes, String recordedBy,
            ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("rabbit_id", rabbitId);
            requestData.put("weight", weight);
            requestData.put("date", date);
            requestData.put("notes", notes);
            requestData.put("recorded_by", recordedBy);
            makePostRequest(ADD_WEIGHT_RECORD_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    // Health records methods
    public static void getHealthRecords(String rabbitId, ApiCallback callback) {
        makeGetRequest(GET_HEALTH_RECORDS_ENDPOINT, "rabbit_id=" + rabbitId, callback);
    }

    public static void addHealthRecord(String rabbitId, String recordDate, String healthStatus, String symptoms,
            String diagnosis, String treatment, String medication, String dosage,
            String vetName, String vetContact, Double cost, String notes, ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("rabbit_id", rabbitId);
            requestData.put("record_date", recordDate);
            requestData.put("health_status", healthStatus);
            requestData.put("symptoms", symptoms);
            requestData.put("diagnosis", diagnosis);
            requestData.put("treatment", treatment);
            requestData.put("medication", medication);
            requestData.put("dosage", dosage);
            requestData.put("vet_name", vetName);
            requestData.put("vet_contact", vetContact);
            if (cost != null)
                requestData.put("cost", cost);
            requestData.put("notes", notes);
            makePostRequest(ADD_HEALTH_RECORD_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    // Mating records methods
    public static void getMatingRecords(String rabbitId, ApiCallback callback) {
        makeGetRequest(GET_MATING_RECORDS_ENDPOINT, "rabbit_id=" + rabbitId, callback);
    }

    public static void addMatingRecord(String rabbitId, String maleRabbitId, String matingDate,
            String expectedDeliveryDate,
            int litterSize, int liveBirths, int stillbirths, String notes, String status, ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("rabbit_id", rabbitId);
            requestData.put("male_rabbit_id", maleRabbitId);
            requestData.put("mating_date", matingDate);
            requestData.put("expected_delivery_date", expectedDeliveryDate);
            requestData.put("litter_size", litterSize);
            requestData.put("live_births", liveBirths);
            requestData.put("stillbirths", stillbirths);
            requestData.put("notes", notes);
            requestData.put("status", status);
            makePostRequest(ADD_MATING_RECORD_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    // Financial methods
    public static void addExpense(String userUid, String expenseDate, String category, String description,
            Double amount, String rabbitId, String supplier, String notes, ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("user_uid", userUid);
            requestData.put("expense_date", expenseDate);
            requestData.put("category", category);
            requestData.put("description", description);
            requestData.put("amount", amount);
            if (rabbitId != null && !rabbitId.isEmpty())
                requestData.put("rabbit_id", rabbitId);
            requestData.put("supplier", supplier);
            requestData.put("notes", notes);
            makePostRequest(ADD_EXPENSE_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    public static void addSale(String rabbitId, String userUid, String saleDate, String buyerName, String buyerContact,
            Double salePrice, Double weightAtSale, String paymentMethod, String notes, ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("rabbit_id", rabbitId);
            requestData.put("user_uid", userUid);
            requestData.put("sale_date", saleDate);
            requestData.put("buyer_name", buyerName);
            requestData.put("buyer_contact", buyerContact);
            requestData.put("sale_price", salePrice);
            if (weightAtSale != null)
                requestData.put("weight_at_sale", weightAtSale);
            requestData.put("payment_method", paymentMethod);
            requestData.put("notes", notes);
            makePostRequest(ADD_SALE_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    // Reports methods
    public static void getDashboardStats(String userUid, ApiCallback callback) {
        makeGetRequest(GET_DASHBOARD_STATS_ENDPOINT, "user_uid=" + userUid, callback);
    }

    // Admin methods
    public static void getAllUsers(String adminKey, ApiCallback callback) {
        makeGetRequest(GET_ALL_USERS_ENDPOINT, "admin_key=" + adminKey, callback);
    }

    public static void changeAdminKey(String currentKey, String newKey, ApiCallback callback) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("current_key", currentKey);
            requestData.put("new_key", newKey);
            makePostRequest(CHANGE_ADMIN_KEY_ENDPOINT, requestData, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }
}