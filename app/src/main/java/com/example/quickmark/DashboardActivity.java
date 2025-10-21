package com.example.quickmark;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private final String BASE_URL = "http://10.111.102.223/QuickMark/api/";

    private Button btnLogout, btnClockIn;
    private TextView txtFullName, courseNameTextView, locationTextView;

    private String currentSessionId = null;
    private String userId;

    private FusedLocationProviderClient fusedLocationClient;
    private double currentLat = 0.0;
    private double currentLng = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        txtFullName = findViewById(R.id.txtFullName);
        courseNameTextView = findViewById(R.id.courseNameTextView);
        locationTextView = findViewById(R.id.locationTextView);
        btnLogout = findViewById(R.id.btnLogout);
        btnClockIn = findViewById(R.id.btnClockIn);


        userId =
                getIntent().getStringExtra("user_id");
        // Status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#0B5C51"));

        // Set full name
        String fullName = getIntent().getStringExtra("fullName");
        if (fullName == null || fullName.isEmpty()) fullName = "User";
        txtFullName.setText("Welcome, " + fullName + "!");

        // Disable clock-in by default
        btnClockIn.setEnabled(false);

        // Logout button
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        });

        // Clock-in button
        btnClockIn.setOnClickListener(v -> {
            if (currentSessionId != null) {
                clockIn();
            } else {
                Toast.makeText(this, "No active session to clock in", Toast.LENGTH_SHORT).show();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();
    }

    // Request location updates
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                currentLat = locationResult.getLastLocation().getLatitude();
                currentLng = locationResult.getLastLocation().getLongitude();
                fetchOpenSessions();
            }
        }, getMainLooper());
    }

    // Fetch currently open sessions from server
    private void fetchOpenSessions() {
        String url = BASE_URL + "get_open_sessions.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.getString("status");

                        if (status.equals("success")) {
                            JSONArray sessions = json.getJSONArray("sessions");
                            boolean sessionInRange = false;

                            for (int i = 0; i < sessions.length(); i++) {
                                JSONObject s = sessions.getJSONObject(i);

                                double sessionLat = s.getDouble("lat");
                                double sessionLng = s.getDouble("lng");
                                double radius = s.getDouble("radius_m");
                                double distance = s.getDouble("distance_m");
                                String courseName = s.getString("course_name");

                                Log.d("DEBUG_SESSION", "Session: " + courseName + " | Distance: " + distance + "m | Radius: " + radius + "m");

                                if (distance <= radius) {
                                    currentSessionId = s.getString("session_id");
                                    courseNameTextView.setText(courseName);
                                    locationTextView.setText("In Range");
                                    btnClockIn.setEnabled(true);
                                    sessionInRange = true;
                                    break;
                                }
                            }

                            if (!sessionInRange) {
                                currentSessionId = null;
                                courseNameTextView.setText("No session in range");
                                locationTextView.setText("");
                                btnClockIn.setEnabled(false);
                            }

                        } else {
                            currentSessionId = null;
                            courseNameTextView.setText(json.optString("message", "No session in range"));
                            locationTextView.setText("");
                            btnClockIn.setEnabled(false);
                            Log.d("DEBUG_SESSION", "Debug info: " + json.optJSONObject("debug"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        currentSessionId = null;
                        courseNameTextView.setText("Error parsing sessions");
                        locationTextView.setText("");
                        btnClockIn.setEnabled(false);
                    }
                },
                error -> {
                    currentSessionId = null;
                    courseNameTextView.setText("Error fetching sessions");
                    locationTextView.setText("");
                    btnClockIn.setEnabled(false);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("lat", String.valueOf(currentLat));
                params.put("lng", String.valueOf(currentLng));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


    // Handle no session or error
    private void noSessionInRange(String message){
        currentSessionId = null;
        courseNameTextView.setText(message);
        locationTextView.setText("");
        btnClockIn.setEnabled(false);
    }

    // Clock-in API
    private void clockIn() {
        if (currentSessionId == null) {
            Toast.makeText(this, "No active session to clock in", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "create_clock_in.php";
        String deviceFingerprint = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.getString("status");
                        String message = json.optString("message", "");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (status.equals("success")) {
                            btnClockIn.setEnabled(false); // prevent double click
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing clock-in response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error connecting to server", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("session_id", currentSessionId);
                params.put("lat", String.valueOf(currentLat));
                params.put("lng", String.valueOf(currentLng));
                params.put("device_fingerprint", deviceFingerprint);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission required to detect sessions", Toast.LENGTH_LONG).show();
            }
        }
    }
}
