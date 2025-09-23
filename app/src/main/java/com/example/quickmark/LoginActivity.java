package com.example.quickmark;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView txtRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Link XML fields
        edtEmail = findViewById(R.id.edtEmail); // you can rename edtId to edtEmail in XML for clarity
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        // Login button click
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        // Register text click
        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        // Make sure this URL points to your PHP login script on XAMPP
        String url = "http://192.168.97.223/QuickMark/api/login_api.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    response = response.trim();
                    switch (response) {
                        case "success":
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            // Go to student dashboard
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("email", email); // optionally pass user info
                            startActivity(intent);
                            finish();
                            break;
                        case "Incorrect password":
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                            break;
                        case "Account pending approval":
                            Toast.makeText(LoginActivity.this, "Your account is pending admin approval", Toast.LENGTH_LONG).show();
                            break;
                        case "User not found":
                            Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_LONG).show();
                            break;
                        case "Missing parameters":
                            Toast.makeText(LoginActivity.this, "Server error: missing parameters", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "Unknown response: " + response, Toast.LENGTH_LONG).show();
                            break;
                    }
                },
                error -> Toast.makeText(LoginActivity.this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Must match PHP expected parameters
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("role", "student");
                return params;
            }
        };

        queue.add(request);
}
}
