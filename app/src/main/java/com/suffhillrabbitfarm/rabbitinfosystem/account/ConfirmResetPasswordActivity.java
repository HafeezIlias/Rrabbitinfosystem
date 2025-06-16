package com.suffhillrabbitfarm.rabbitinfosystem.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.helpers.MiscHelper;

import org.json.JSONObject;

public class ConfirmResetPasswordActivity extends AppCompatActivity {

    private EditText editTextToken, editTextNewPassword, editTextConfirmPassword;
    private Button buttonConfirmReset;
    private ImageView imageViewBack;
    private TextView textViewEmail;
    private Dialog loadingDialog;

    private String resetToken;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reset_password);

        // Get data from intent
        resetToken = getIntent().getStringExtra("reset_token");
        email = getIntent().getStringExtra("email");

        initViews();
    }

    private void initViews() {
        editTextToken = findViewById(R.id.editTextToken);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonConfirmReset = findViewById(R.id.buttonConfirmReset);
        imageViewBack = findViewById(R.id.backArrow);
        textViewEmail = findViewById(R.id.textViewEmail);

        // Pre-fill email and token if available
        if (email != null) {
            textViewEmail.setText("Reset password for: " + email);
        }
        if (resetToken != null) {
            editTextToken.setText(resetToken);
        }

        buttonConfirmReset.setOnClickListener(view -> confirmPasswordReset());
        imageViewBack.setOnClickListener(view -> finish());
    }

    private void confirmPasswordReset() {
        String token = editTextToken.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Validation
        if (token.isEmpty()) {
            editTextToken.setError("Reset token is required");
            return;
        }

        if (newPassword.isEmpty()) {
            editTextNewPassword.setError("New password is required");
            return;
        }

        if (newPassword.length() < 6) {
            editTextNewPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Show loading dialog
        loadingDialog = MiscHelper.openNetLoaderDialog(this);

        // Call verify reset token API
        ApiHelper.verifyResetToken(token, newPassword, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                loadingDialog.dismiss();

                Toast.makeText(ConfirmResetPasswordActivity.this,
                        "Password updated successfully! Please login with your new password.",
                        Toast.LENGTH_LONG).show();

                // Navigate back to login
                Intent intent = new Intent(ConfirmResetPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                loadingDialog.dismiss();
                Toast.makeText(ConfirmResetPasswordActivity.this,
                        "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}