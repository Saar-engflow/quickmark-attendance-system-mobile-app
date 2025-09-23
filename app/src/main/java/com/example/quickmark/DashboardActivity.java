package com.example.quickmark;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;

public class DashboardActivity extends AppCompatActivity {
    Button btnLogout, btnClockIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        Window window  = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#0B5C51"));

        btnLogout = findViewById(R.id.btnLogout);
        btnClockIn = findViewById(R.id.btnClockIn);

        // Logout logic
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // close Dashboard so back won’t return here
        });

        // Clock In button logic
        btnClockIn.setOnClickListener(v -> {
            Toast.makeText(this, "Clocked in successfully!", Toast.LENGTH_SHORT).show();
            // TODO: Later connect to PHP/MySQL to save attendance
   });
}
}
