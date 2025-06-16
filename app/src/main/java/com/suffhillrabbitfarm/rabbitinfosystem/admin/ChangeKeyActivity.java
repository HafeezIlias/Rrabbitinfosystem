package com.suffhillrabbitfarm.rabbitinfosystem.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;

import org.json.JSONObject;

public class ChangeKeyActivity extends AppCompatActivity {

    EditText editTextCurrentKey, editTextNewKey;
    Button buttonUpdateKey;
    TextView textViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_key);

        initViews();
    }

    private void initViews() {
        editTextCurrentKey = findViewById(R.id.editTextCurrentKey);
        editTextNewKey = findViewById(R.id.editTextNewKey);
        buttonUpdateKey = findViewById(R.id.buttonUpdate);
        textViewBack = findViewById(R.id.backArrow);

        buttonUpdateKey.setOnClickListener(view -> validateAndUpdateKey());
        textViewBack.setOnClickListener(view -> finish());
    }

    private void validateAndUpdateKey() {
        String currentKey = editTextCurrentKey.getText().toString().trim();
        String newKey = editTextNewKey.getText().toString().trim();

        if (currentKey.isEmpty()) {
            editTextCurrentKey.setError("Current key required");
            return;
        }
        if (newKey.isEmpty()) {
            editTextNewKey.setError("New key required");
            return;
        }

        ApiHelper.changeAdminKey(currentKey, newKey, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(ChangeKeyActivity.this, "Admin key updated successfully", Toast.LENGTH_SHORT).show();
                // Clear the fields
                editTextCurrentKey.setText("");
                editTextNewKey.setText("");
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChangeKeyActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
