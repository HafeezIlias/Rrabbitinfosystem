package com.suffhillrabbitfarm.rabbitinfosystem;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;

import org.json.JSONObject;

public class DebugActivity extends AppCompatActivity {

    private TextView debugInfo;
    private Button testButton, loginTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create simple layout programmatically
        setContentView(R.layout.activity_main); // Reuse existing layout temporarily

        // Find existing views or create new ones
        debugInfo = new TextView(this);
        debugInfo.setText("Debug Information:\n\nClick buttons to test API");
        debugInfo.setPadding(20, 20, 20, 20);

        testButton = new Button(this);
        testButton.setText("Test Basic Connection");
        testButton.setOnClickListener(v -> testBasicConnection());

        loginTestButton = new Button(this);
        loginTestButton.setText("Test Login API");
        loginTestButton.setOnClickListener(v -> testLoginAPI());

        // Add views to layout (simple approach)
        Toast.makeText(this, "Debug Activity Started. Check LogCat for detailed logs.", Toast.LENGTH_LONG).show();
    }

    private void testBasicConnection() {
        debugInfo.setText("Testing basic connection...\nCheck LogCat for details");

        ApiHelper.makeGetRequest("debug/test.php", null, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    debugInfo.setText("✓ SUCCESS!\n\nConnection working!\n\n" + response.toString());
                    Toast.makeText(DebugActivity.this, "Connection successful!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    debugInfo.setText("✗ ERROR:\n\n" + error);
                    Toast.makeText(DebugActivity.this, "Connection failed!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void testLoginAPI() {
        debugInfo.setText("Testing login API...\nCheck LogCat for details");

        ApiHelper.login("test@example.com", "wrongpassword", new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    debugInfo.setText("✓ LOGIN API WORKING!\n\n" + response.toString());
                    Toast.makeText(DebugActivity.this, "Login API working!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // Error is expected with wrong credentials, but API should respond
                    if (error.contains("Invalid email or password")) {
                        debugInfo.setText("✓ LOGIN API WORKING!\n\nGot expected error: " + error);
                        Toast.makeText(DebugActivity.this, "Login API working (expected error)!", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        debugInfo.setText("✗ LOGIN API ERROR:\n\n" + error);
                        Toast.makeText(DebugActivity.this, "Login API failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}