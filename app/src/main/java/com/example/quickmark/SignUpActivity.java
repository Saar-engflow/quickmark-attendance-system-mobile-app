package com.example.quickmark;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {



    EditText edtUserId, edtFirstName, edtLastName, edtEmail, edtPassword, edtConfirmPassword, edtInstitute, edtRole;
    Button btnSignUp;
    TextView txtLoginHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtUserId = findViewById(R.id.edtUserId);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtInstitute = findViewById(R.id.edtInstitute);
        edtRole = findViewById(R.id.edtRole);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtLoginHere = findViewById(R.id.txtLoginHere);

        btnSignUp.setOnClickListener(v -> {
            String userId = edtUserId.getText().toString().trim();
            String firstName = edtFirstName.getText().toString().trim();
            String lastName = edtLastName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();
            String institute = edtInstitute.getText().toString().trim();
            String role = "student"; // fixed role

            // Validation
            if (userId.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                    email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                    institute.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Fill up all fields", Toast.LENGTH_SHORT).show();
                return;
            }

// Email validation
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignUpActivity.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

// Strong password validation
            String passwordPattern = "^(?=.[a-z])(?=.[A-Z])(?=.*\\d).{6,}$";
            if (!password.matches(passwordPattern)) {
                Toast.makeText(SignUpActivity.this,
                        "Password must be at least 6 characters, contain a capital letter, small letter, and a number",
                        Toast.LENGTH_LONG).show();
                return;
            }

// Confirm password
            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
return;
            }


            // URL to your PHP script
            String url = "http://192.168.97.223/QuickMark/views/auth/register.php?role=student";

            // Use Volley
            com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);
            com.android.volley.toolbox.StringRequest stringRequest = new com.android.volley.toolbox.StringRequest(
                    com.android.volley.Request.Method.POST,
                    url,
                    response -> {
                        // Handle PHP response
                        Toast.makeText(SignUpActivity.this, "Server Response: " + response, Toast.LENGTH_LONG).show();
                        // If successful, go to login
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        // Handle errors
                        Toast.makeText(SignUpActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                protected java.util.Map<String, String> getParams() {
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    params.put("signup", "true");
                    params.put("userid", userId);
                    params.put("firstName", firstName);
                    params.put("lastName", lastName);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("confirm_password", confirmPassword);
                    params.put("institute", institute);
                    params.put("role", role);
                    return params;
                }
            };

            queue.add(stringRequest);
        });


        // "Login here" TextView Logic
        txtLoginHere.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
   });
}
}
