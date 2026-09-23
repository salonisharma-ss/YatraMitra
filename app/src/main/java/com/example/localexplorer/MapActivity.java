package com.example.localexplorer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnBack;
    private double latitude;
    private double longitude;
    private String placeName;
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Log.d(TAG, "===== MapActivity Started =====");

        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                finish();
            });
        }

        // Get place info from intent
        try {
            if (getIntent() != null) {
                latitude = getIntent().getDoubleExtra("latitude", 0);
                longitude = getIntent().getDoubleExtra("longitude", 0);
                placeName = getIntent().getStringExtra("placeName");

                Log.d(TAG, "Intent received:");
                Log.d(TAG, "  Place: " + placeName);
                Log.d(TAG, "  Latitude: " + latitude);
                Log.d(TAG, "  Longitude: " + longitude);

                // Validate
                if (latitude == 0 || longitude == 0) {
                    Log.e(TAG, "❌ Invalid coordinates!");
                    Toast.makeText(this, "Invalid location!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            } else {
                Log.e(TAG, "❌ No intent received!");
                Toast.makeText(this, "Error: No location data!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Intent parse error: " + e.getMessage());
            Toast.makeText(this, "Error reading intent: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get map fragment
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);

            if (mapFragment != null) {
                Log.d(TAG, "✅ Map fragment found, getting map...");
                mapFragment.getMapAsync(this);
            } else {
                Log.e(TAG, "❌ Map fragment is NULL!");
                Toast.makeText(this, "Map fragment not found!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Map fragment error: " + e.getMessage());
            Toast.makeText(this, "Map error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "✅ onMapReady called");

        try {
            mMap = googleMap;

            // Enable zoom controls
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Set location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    Log.d(TAG, "✅ Location enabled on map");
                }
            } else {
                mMap.setMyLocationEnabled(true);
            }

            // Create location
            LatLng placeLocation = new LatLng(latitude, longitude);
            Log.d(TAG, "📍 Adding marker at: " + placeLocation);

            // Add marker
            mMap.addMarker(new MarkerOptions()
                    .position(placeLocation)
                    .title(placeName != null ? placeName : "Place")
                    .snippet("Latitude: " + latitude + "\nLongitude: " + longitude));

            Log.d(TAG, "✅ Marker added");

            // Move camera
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15));
            Log.d(TAG, "✅ Camera moved");

            Toast.makeText(this, "Map loaded! 🗺️", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "onMapReady error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Map error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}