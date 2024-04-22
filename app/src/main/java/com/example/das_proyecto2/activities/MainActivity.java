package com.example.das_proyecto2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.das_proyecto2.R;
import com.example.das_proyecto2.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Creating a Handler
        Handler handler = new Handler();
        // Delaying execution of the Intent by 3 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Your Intent code goes here
                SessionManager sessionManager = new SessionManager(MainActivity.this);
                Intent intent;
                if (sessionManager.getEmail() == null) {
                    intent = new Intent(MainActivity.this, AuthActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, HomeActivity.class);
                }
                startActivity(intent);
                finish(); // Optional, if you want to close the current activity
            }
        }, 3000); // 3000 milliseconds = 3 seconds
    }
}