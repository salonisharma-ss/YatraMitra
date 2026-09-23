package com.example.localexplorer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editCity;
    private Button btnSave;
    private Button btnBack;
    private TextView profileTitle;
    private LinearLayout statsLayout;
    
    private SharedPreferences sharedPreferences;
    private static final String SESSION_PREF = "session_pref";
    private static final String USER_NAME = "user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editCity = findViewById(R.id.editCity);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        profileTitle = findViewById(R.id.profileTitle);
        statsLayout = findViewById(R.id.statsLayout);

        sharedPreferences = getSharedPreferences(SESSION_PREF, MODE_PRIVATE);

        // Load saved profile
        loadProfile();

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveProfile());
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        displayStats();
    }

    private String getUserKey() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : "guest";
    }

    private void loadProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = getUserKey();
        
        // Fetch from SharedPreferences with User UID prefix
        String name = sharedPreferences.getString(userKey + "_name", "");
        String email = sharedPreferences.getString(userKey + "_email", "");
        String phone = sharedPreferences.getString(userKey + "_phone", "");
        String city = sharedPreferences.getString(userKey + "_city", "");

        // Fallback to Firebase for name/email if SharedPreferences is empty
        if (currentUser != null) {
            if (name.isEmpty()) name = currentUser.getDisplayName();
            if (email.isEmpty()) email = currentUser.getEmail();
        }

        if (editName != null) editName.setText(name != null ? name : "Traveler");
        if (editEmail != null) editEmail.setText(email != null ? email : "");
        if (editPhone != null) editPhone.setText(phone);
        if (editCity != null) editCity.setText(city);

        if (profileTitle != null) {
            profileTitle.setText("👤 " + (name != null && !name.isEmpty() ? name : "Profile"));
        }
    }

    private void saveProfile() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String city = editCity.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }

        String userKey = getUserKey();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userKey + "_name", name);
        editor.putString(userKey + "_email", email);
        editor.putString(userKey + "_phone", phone);
        editor.putString(userKey + "_city", city);
        editor.apply();

        Toast.makeText(this, "Profile updated! ✅", Toast.LENGTH_SHORT).show();
        if (profileTitle != null) {
            profileTitle.setText("👤 " + name);
        }
    }

    private void displayStats() {
        if (statsLayout == null) return;
        statsLayout.removeAllViews();

        FavoritesDatabase favDB = new FavoritesDatabase(this);
        int favoritesCount = favDB.getAllFavorites().size();

        ReviewsDatabase reviewsDB = new ReviewsDatabase(this);
        int reviewsCount = reviewsDB.getAllReviewsCount();

        LinearLayout statsContainer = new LinearLayout(this);
        statsContainer.setOrientation(LinearLayout.HORIZONTAL);
        statsContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        statsContainer.addView(createStatCard("❤️", "Favorites", String.valueOf(favoritesCount)));
        statsContainer.addView(createStatCard("⭐", "Reviews", String.valueOf(reviewsCount)));
        statsLayout.addView(statsContainer);
    }

    private LinearLayout createStatCard(String emoji, String label, String count) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(16, 16, 16, 16);
        card.setBackgroundResource(R.drawable.place_card_bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(8, 8, 8, 8);
        card.setLayoutParams(params);

        TextView emojiView = new TextView(this);
        emojiView.setText(emoji);
        emojiView.setTextSize(24);
        emojiView.setGravity(android.view.Gravity.CENTER);
        card.addView(emojiView);

        TextView countView = new TextView(this);
        countView.setText(count);
        countView.setTextSize(20);
        countView.setTextColor(0xFF1976D2);
        countView.setGravity(android.view.Gravity.CENTER);
        countView.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(countView);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(12);
        labelView.setTextColor(0xFF666666);
        labelView.setGravity(android.view.Gravity.CENTER);
        card.addView(labelView);

        return card;
    }
}
