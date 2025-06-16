package com.suffhillrabbitfarm.rabbitinfosystem.admin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.helpers.MiscHelper;

public class AdminLogin extends AppCompatActivity {
    ImageView backArrow;
    EditText editTextKey;
    Button buttonAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_admin_login);
        initViews();
    }

    private void initViews() {
        backArrow = findViewById(R.id.backArrow);
        editTextKey = findViewById(R.id.editTextKey);
        buttonAccess = findViewById(R.id.buttonAccess);

        backArrow.setOnClickListener(view -> finish());
        buttonAccess.setOnClickListener(view -> checkAccess());
    }

    private void checkAccess() {
        String key = editTextKey.getText().toString().trim();

        if (key.isEmpty()) {
            editTextKey.setError("Key required");
            return;
        }

        Dialog loadingDialog = MiscHelper.openNetLoaderDialog(this);

        // TODO: Replace with API call to validate admin key
        // For now, using hardcoded admin key for testing
        String validAdminKey = "123";

        // Simulate network delay
        new Thread(() -> {
            try {
                Thread.sleep(1000); // 1 second delay

                runOnUiThread(() -> {
                    loadingDialog.dismiss();

                    if (validAdminKey.equals(key)) {
                        Toast.makeText(AdminLogin.this, "Access successful", Toast.LENGTH_SHORT).show();
                        openMainActivity();
                    } else {
                        Toast.makeText(AdminLogin.this, "Access Failed", Toast.LENGTH_SHORT).show();
                        editTextKey.setError("Invalid admin key");
                    }
                });

            } catch (InterruptedException e) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(AdminLogin.this, "Error occurred", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void openMainActivity() {
        Intent intent = new Intent(AdminLogin.this, AdminMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
