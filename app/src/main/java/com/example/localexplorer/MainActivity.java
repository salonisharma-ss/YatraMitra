package com.example.localexplorer;

import android.app.ProgressDialog;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button btnRestaurant, btnHotel, btnTouristSpot, btnEvents;
    private EditText searchBar;
    private LinearLayout placesList;
    private FirebaseAuth firebaseAuth;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String GOOGLE_PLACES_API_KEY = "YOUR_GOOGLE_MAPS_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GooglePlacesHelper.initializePlaces(this, GOOGLE_PLACES_API_KEY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        
        setupLocationCallback();
        setupNavigationButtons();
        displayUserGreeting();
        initializeViews();
        setupButtonListeners();
        setupSearchListener();

        requestLocationPermission();
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        updatePopularPlaces(location);
                    }
                }
            }
        };
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateDistanceMeters(200)
                .build();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                updatePopularPlaces(location);
            }
        });
    }

    private void updatePopularPlaces(Location location) {
        String cityName = getCurrentCity(location);
        final String displayCity = (cityName != null) ? cityName : "Your City";

        runOnUiThread(() -> {
            placesList.removeAllViews();
            addHeaderToPlacesList("🌟 Famous in " + displayCity);
        });

        GooglePlacesHelper.searchPlacesByText(this, "Famous Tourist Spots in " + displayCity, new GooglePlacesHelper.PlacesCallback() {
            @Override
            public void onSuccess(List<PlaceModel> places) {
                runOnUiThread(() -> {
                    List<PlaceModel> top3 = new ArrayList<>();
                    for (int i = 0; i < Math.min(3, places.size()); i++) {
                        top3.add(places.get(i));
                    }
                    displayPlacesList(top3, "tourist_attraction", false);
                });
            }

            @Override
            public void onError(String error) {
                Log.e("MainActivity", "Load Error: " + error);
            }
        });
    }

    private String getCurrentCity(Location location) {
        String cityName = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                cityName = addresses.get(0).getLocality();
                if (cityName == null) cityName = addresses.get(0).getSubAdminArea();
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Geocoder error: " + e.getMessage());
        }
        return cityName;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void setupNavigationButtons() {
        findViewById(R.id.btnBudget).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BudgetTrackerActivity.class)));
        findViewById(R.id.btnFavorites).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FavoritesActivity.class)));
        findViewById(R.id.btnProfile).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, UserProfileActivity.class)));
        findViewById(R.id.btnLogout).setOnClickListener(v -> performLogout());
    }

    private void performLogout() {
        firebaseAuth.signOut();
        SharedPreferences.Editor editor = getSharedPreferences("session_pref", MODE_PRIVATE).edit();
        editor.clear().apply();
        startActivity(new Intent(MainActivity.this, AuthActivity.class));
        finish();
    }

    private void initializeViews() {
        btnRestaurant = findViewById(R.id.btnRestaurant);
        btnHotel = findViewById(R.id.btnHotel);
        btnTouristSpot = findViewById(R.id.btnTouristSpot);
        btnEvents = findViewById(R.id.btnEvents);
        searchBar = findViewById(R.id.searchBar);
        placesList = findViewById(R.id.placesList);
    }

    private void setupButtonListeners() {
        btnRestaurant.setOnClickListener(v -> getLocationAndFetchPlaces("restaurant"));
        btnHotel.setOnClickListener(v -> getLocationAndFetchPlaces("lodging"));
        btnTouristSpot.setOnClickListener(v -> getLocationAndFetchPlaces("tourist_attraction"));
        btnEvents.setOnClickListener(v -> fetchUpcomingEvents());
    }

    private void fetchUpcomingEvents() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Discovering local events...");
        pd.show();

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                pd.dismiss();
                showToast("Unable to get current location");
                return;
            }

            String cityName = getCurrentCity(location);
            String query = (cityName != null) ? "Upcoming events in " + cityName : "Upcoming events nearby";

            GooglePlacesHelper.searchPlacesByText(this, query, new GooglePlacesHelper.PlacesCallback() {
                @Override
                public void onSuccess(List<PlaceModel> places) {
                    runOnUiThread(() -> {
                        pd.dismiss();
                        placesList.removeAllViews();
                        addHeaderToPlacesList("📅 Upcoming Events in " + (cityName != null ? cityName : "your city"));
                        displayPlacesList(places, "event", true);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        pd.dismiss();
                        showToast(error);
                    });
                }
            });
        });
    }

    private void setupSearchListener() {
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            String searchQuery = searchBar.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                searchPlaces(searchQuery);
            }
            return false;
        });
    }

    private void getLocationAndFetchPlaces(String placeType) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Finding " + placeType + "s...");
        pd.show();

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                pd.dismiss();
                showToast("Unable to get current location");
                return;
            }

            GooglePlacesHelper.searchNearbyPlaces(this, location, placeType, new GooglePlacesHelper.PlacesCallback() {
                @Override
                public void onSuccess(List<PlaceModel> places) {
                    runOnUiThread(() -> {
                        pd.dismiss();
                        placesList.removeAllViews();
                        addHeaderToPlacesList("📍 Results for " + placeType);
                        displayPlacesList(places, placeType, true);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        pd.dismiss();
                        showToast(error);
                    });
                }
            });
        });
    }

    private void searchPlaces(String query) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Searching...");
        pd.show();

        GooglePlacesHelper.searchPlacesByText(this, query, new GooglePlacesHelper.PlacesCallback() {
            @Override
            public void onSuccess(List<PlaceModel> places) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    placesList.removeAllViews();
                    addHeaderToPlacesList("🔍 Search Results");
                    displayPlacesList(places, "search", true);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    pd.dismiss();
                    showToast(error);
                });
            }
        });
    }

    private void displayPlacesList(List<PlaceModel> places, String type, boolean clearFirst) {
        if (clearFirst) placesList.removeAllViews();
        if (places.isEmpty()) {
            showNoResults("No " + (type.equals("event") ? "events" : "places") + " found nearby.");
            return;
        }

        String emoji = getEmojiForPlace(type);
        for (PlaceModel place : places) {
            LinearLayout cardLayout = new LinearLayout(this);
            cardLayout.setOrientation(LinearLayout.HORIZONTAL);
            cardLayout.setPadding(32, 40, 32, 40);
            cardLayout.setGravity(Gravity.CENTER_VERTICAL);
            cardLayout.setBackgroundResource(R.drawable.place_card_bg);
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 8, 0, 24);
            cardLayout.setLayoutParams(lp);
            cardLayout.setElevation(6f);

            TextView emojiView = new TextView(this);
            emojiView.setText(emoji);
            emojiView.setTextSize(34);
            emojiView.setPadding(0, 0, 32, 0);

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            
            TextView name = new TextView(this);
            name.setText(place.getName());
            name.setTextSize(17);
            name.setTypeface(null, Typeface.BOLD);
            name.setTextColor(Color.parseColor("#1A237E"));

            TextView rating = new TextView(this);
            rating.setText("⭐ " + place.getRating() + " (" + place.getReviews() + ")");
            rating.setTextSize(13);
            rating.setTextColor(Color.parseColor("#FF6F00"));
            rating.setPadding(0, 4, 0, 4);

            TextView dist = new TextView(this);
            if (place.getDistance() > 0) {
                dist.setText(String.format("📍 %.1f km away", place.getDistance()));
            } else {
                dist.setText("📍 Nearby");
            }
            dist.setTextSize(12);
            dist.setTextColor(Color.parseColor("#757575"));

            info.addView(name);
            if (!type.equals("event")) {
                info.addView(rating);
            }
            info.addView(dist);

            TextView chevron = new TextView(this);
            chevron.setText("❯");
            chevron.setTextSize(16);
            chevron.setTextColor(Color.parseColor("#BDBDBD"));

            cardLayout.addView(emojiView);
            cardLayout.addView(info);
            cardLayout.addView(chevron);
            
            cardLayout.setOnClickListener(v -> openPlaceDetail(place));
            placesList.addView(cardLayout);
        }
    }

    private void openPlaceDetail(PlaceModel place) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        intent.putExtra("latitude", place.getLatitude());
        intent.putExtra("longitude", place.getLongitude());
        intent.putExtra("name", place.getName());
        intent.putExtra("address", place.getAddress());
        intent.putExtra("rating", String.valueOf(place.getRating()));
        intent.putExtra("reviews", place.getReviews());
        intent.putExtra("price", place.getPrice());
        intent.putExtra("distance", place.getDistance());
        intent.putExtra("placeId", place.getPlaceId());
        intent.putExtra("photoUrl", place.getPhotoUrl()); // ✅ Added this
        startActivity(intent);
    }

    private void addHeaderToPlacesList(String title) {
        TextView header = new TextView(this);
        header.setText(title);
        header.setTextSize(18);
        header.setTypeface(Typeface.DEFAULT_BOLD);
        header.setPadding(10, 20, 10, 20);
        header.setTextColor(Color.parseColor("#455A64"));
        placesList.addView(header);
    }

    private void showNoResults(String msg) {
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setPadding(30, 30, 30, 30);
        tv.setGravity(Gravity.CENTER);
        placesList.addView(tv);
    }

    private String getEmojiForPlace(String type) {
        if (type.contains("restaurant")) return "🍱";
        if (type.contains("hotel") || type.contains("lodging")) return "🏨";
        if (type.contains("attraction")) return "📸";
        if (type.contains("event")) return "🎟️";
        return "📍";
    }

    private void displayUserGreeting() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            TextView title = findViewById(R.id.appTitle);
            if (title != null) {
                title.setText("👋 Welcome, " + (name != null ? name : "Explorer") + "!");
                title.setTextColor(Color.parseColor("#1565C0"));
                title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }
}
