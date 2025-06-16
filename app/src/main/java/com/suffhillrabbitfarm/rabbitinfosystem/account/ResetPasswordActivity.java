package com.suffhillrabbitfarm.rabbitinfosystem.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.helpers.MiscHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button buttonResetPassword;
    private ImageView imageViewBackToLogin;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.edEmail);
        buttonResetPassword = findViewById(R.id.buttonGetResetLink);
        imageViewBackToLogin = findViewById(R.id.backArrow);

        buttonResetPassword.setOnClickListener(view -> resetPassword());
        imageViewBackToLogin.setOnClickListener(view -> finish());
    }

    private void resetPassword() {
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email Required");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            return;
        }

        // Show loading dialog
        loadingDialog = MiscHelper.openNetLoaderDialog(this);

        // Call reset password API
        ApiHelper.requestPasswordReset(email, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                loadingDialog.dismiss();

                try {
                    String message = response.getString("message");
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();

                    // Check if we got a reset token (for demo purposes)
                    if (response.has("data") && response.getJSONObject("data").has("reset_token")) {
                        String resetToken = response.getJSONObject("data").getString("reset_token");

                        // For demo: show the reset token in a toast (REMOVE IN PRODUCTION!)
                        Toast.makeText(ResetPasswordActivity.this,
                                "Demo: Reset token = " + resetToken.substring(0, 8) + "...",
                                Toast.LENGTH_LONG).show();

                        // Navigate to password reset confirmation screen
                        Intent intent = new Intent(ResetPasswordActivity.this, ConfirmResetPasswordActivity.class);
                        intent.putExtra("reset_token", resetToken);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        // Normal case: email sent, go back to login
                        finish();
                    }

                } catch (JSONException e) {
                    Toast.makeText(ResetPasswordActivity.this,
                            "Reset request sent successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onError(String error) {
                loadingDialog.dismiss();
                Toast.makeText(ResetPasswordActivity.this,
                        "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}