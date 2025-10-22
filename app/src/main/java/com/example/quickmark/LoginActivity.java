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
import org.json.JSONException;
import org.json.JSONObject;
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
        String url = "http://192.168.10.184/QuickMark/api/login_api.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // Parse JSON response from your updated PHP API
                        JSONObject jsonObject = new JSONObject(response.trim());

                        if (jsonObject.getString("status").equals("success")) {
                            String fullName = jsonObject.getString("fullName");
                            String userId = jsonObject.getString("user_id");
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Go to student dashboard and pass fullName
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("fullName", fullName);
                            intent.putExtra("user_id", userId);
                            startActivity(intent);
                            finish();

                        } else {
                            // Handle errors from API
                            String message = jsonObject.getString("message");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
