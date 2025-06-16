package com.suffhillrabbitfarm.rabbitinfosystem.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class RegistrationActivity extends AppCompatActivity {
    EditText editTextName, editTextEmail, editTextPhoneNumber,
            editTextPassword, editTextConfirmPassword;
    Button buttonRegister;
    TextView textViewLogin;
    CheckBox checkBoxPatient, checkBoxDoctor;

    LoginData loginData;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        loginData = new LoginData(this);
        initViews();
    }

    // connect UI elements
    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
        checkBoxPatient = findViewById(R.id.checkBoxPatient);
        checkBoxDoctor = findViewById(R.id.checkBoxDoctor);

        checkBoxDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxDoctor.setChecked(true); // check doctor checkBox
                checkBoxPatient.setChecked(false); // unCheck patient checkBox
            }
        });

        checkBoxPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxDoctor.setChecked(false);
                checkBoxPatient.setChecked(true);
            }
        });

        // if textViewLogin click finish registration activity and go back
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateAndRegister();
            }
        });
    }

    private void ValidateAndRegister() {
        UserModel userModel = new UserModel();
        userModel.setUserName(editTextName.getText().toString().trim());
        userModel.setEmail(editTextEmail.getText().toString().trim());
        userModel.setPhoneNumber(editTextPhoneNumber.getText().toString().trim());
        userModel.setStatus("Active");

        if (checkBoxPatient.isChecked()) {
            userModel.setAccountType(ApiHelper.STAFF_ACCOUNT);
        } else {
            userModel.setAccountType(ApiHelper.MANAGER_ACCOUNT);
        }

        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if (userModel.getUserName().isEmpty()) {
            editTextName.setError("Name Required");
        } else if (userModel.getEmail().isEmpty()) {
            editTextEmail.setError("Email Required");
        } else if (userModel.getPhoneNumber().isEmpty()) {
            editTextPhoneNumber.setError("Phone Number required");
        } else if (password.length() < 5) {
            editTextPassword.setError("Password length must be greater than 5 characters");
        } else if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Wrong confirm password");
        } else {
            registerUser(userModel, password);
        }
    }

    private void registerUser(UserModel userModel, String password) {
        dialog = MiscHelper.openNetLoaderDialog(this);
        Toast.makeText(this, "Creating account", Toast.LENGTH_SHORT).show();

        ApiHelper.register(
                userModel.getEmail(),
                password,
                userModel.getUserName(),
                userModel.getPhoneNumber(),
                userModel.getAccountType(),
                new ApiHelper.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        dialog.dismiss();
                        try {
                            JSONObject userData = response.getJSONObject("data");

                            // Update userModel with response data
                            userModel.setEmail(userData.getString("email"));
                            userModel.setUserName(userData.getString("user_name"));
                            userModel.setPhoneNumber(userData.getString("phone_number"));
                            userModel.setAccountType(userData.getString("account_type"));

                            // Save login data locally
                            loginData.setAuth(userModel);
                            loginData.setUserUid(userData.getString("uid"));

                            Toast.makeText(RegistrationActivity.this, "Account created successfully",
                                    Toast.LENGTH_SHORT).show();
                            startNewActivity();

                        } catch (JSONException e) {
                            Toast.makeText(RegistrationActivity.this, "Error parsing registration response",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        dialog.dismiss();
                        Toast.makeText(RegistrationActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startNewActivity() {
        Intent intent = new Intent(RegistrationActivity.this, ChoiceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}