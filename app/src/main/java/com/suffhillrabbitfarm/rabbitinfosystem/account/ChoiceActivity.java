package com.suffhillrabbitfarm.rabbitinfosystem.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.MainActivity;
import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.ReportActivity;
import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;
import com.suffhillrabbitfarm.rabbitinfosystem.models.UserModel;

public class ChoiceActivity extends AppCompatActivity {

    private Button buttonProfile, buttonReport;
    private TextView textViewWelcome, textViewSubtitle;
    private LoginData loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        initData();
        initViews();
        setupUserInfo();
    }

    private void initData() {
        loginData = new LoginData(this);
    }

    private void initViews() {
        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewSubtitle = findViewById(R.id.textViewSubtitle);
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonReport = findViewById(R.id.buttonReport);

        buttonProfile.setOnClickListener(view -> {
            startActivity(new Intent(ChoiceActivity.this, MainActivity.class));
            finish();
        });

        buttonReport.setOnClickListener(view -> {
            startActivity(new Intent(ChoiceActivity.this, ReportActivity.class));
        });
    }

    private void setupUserInfo() {
        if (loginData.isLoggedIn()) {
            UserModel userModel = loginData.getLoginType();
            if (userModel != null && userModel.getUserName() != null && !userModel.getUserName().isEmpty()) {
                textViewWelcome.setText("Welcome, " + userModel.getUserName() + "!");
                textViewSubtitle.setText("Choose an option to continue");
            } else {
                textViewWelcome.setText("Welcome!");
                textViewSubtitle.setText("Choose an option to continue");
            }
        } else {
            // User not logged in, redirect to login
            startActivity(new Intent(ChoiceActivity.this, SelectionActivity.class));
            finish();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChoiceActivity.this, MainActivity.class));
        finish();
    }
}