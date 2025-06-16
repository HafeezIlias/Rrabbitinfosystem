package com.suffhillrabbitfarm.rabbitinfosystem.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.suffhillrabbitfarm.rabbitinfosystem.R;
import com.suffhillrabbitfarm.rabbitinfosystem.data.LoginData;

public class SplashScreen extends AppCompatActivity {

    LoginData loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        loginData = new LoginData(this);
        runThreadDelay();
    }

    private void runThreadDelay() {
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(4000);
                    if (loginData.isLoggedIn()) {
                        // User is logged in, redirect to ChoiceActivity
                        startActivity(new Intent(SplashScreen.this, ChoiceActivity.class));
                    } else {
                        // User not logged in, redirect to SelectionActivity
                        startActivity(new Intent(SplashScreen.this, SelectionActivity.class));
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}