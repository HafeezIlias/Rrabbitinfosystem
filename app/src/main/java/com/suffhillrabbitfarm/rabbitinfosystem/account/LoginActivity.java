package com.suffhillrabbitfarm.rabbitinfosystem.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.data.ApiHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;
import com.suffhillrabbitfarm.rabbitinfosystem.helpers.MiscHelper;
import com.suffhillrabbitfarm.rabbitinfosystem.models.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    TextView textViewForgetPassword,
            textViewDoctorRegistration;
    Button buttonLogin;
    LoginData loginData;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginData = new LoginData(this);
        initView();
    }

    private void initView() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewForgetPassword = findViewById(R.id.textViewForgetPassword);
        textViewDoctorRegistration = findViewById(R.id.textViewDoctorRegistration);
        buttonLogin = findViewById(R.id.buttonLogin);

        textViewForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        textViewDoctorRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
    }

    private void doLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            return;
        }

        // Show loading dialog
        dialog = MiscHelper.openNetLoaderDialog(this);

        // Call API login
        ApiHelper.login(email, password, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                dialog.dismiss();
                try {
                    JSONObject userData = response.getJSONObject("data");

                    // Create UserModel from response
                    UserModel userModel = new UserModel();
                    userModel.setEmail(userData.getString("email"));
                    userModel.setUserName(userData.getString("user_name"));
                    userModel.setPhoneNumber(userData.getString("phone_number"));
                    userModel.setAccountType(userData.getString("account_type"));

                    // Save login data locally
                    loginData.setAuth(userModel);
                    loginData.setUserUid(userData.getString("uid")); // Store UID separately

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    // Navigate to choice activity
                    Intent intent = new Intent(LoginActivity.this, ChoiceActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Error parsing login response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}