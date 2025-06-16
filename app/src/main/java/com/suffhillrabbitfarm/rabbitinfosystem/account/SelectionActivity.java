


package com.suffhillrabbitfarm.rabbitinfosystem.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.admin.AdminLogin;


public class SelectionActivity extends AppCompatActivity {
    Button buttonChildLogin,buttonAdminLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_selection);
        initViews();
    }
    private void initViews() {
        buttonChildLogin=findViewById(R.id.buttonChildLogin);
        buttonAdminLogin=findViewById(R.id.buttonAdminLogin);
        buttonAdminLogin.setOnClickListener(view -> {
            startActivity(new Intent(SelectionActivity.this, AdminLogin.class));
        });

        buttonChildLogin.setOnClickListener(view -> {
            startActivity(new Intent(SelectionActivity.this, LoginActivity.class));
        });
    }
}