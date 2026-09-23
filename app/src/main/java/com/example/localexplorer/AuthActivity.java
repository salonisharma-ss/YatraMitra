package com.example.localexplorer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegister;
    private TextView appName;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private static final String SESSION_PREF = "session_pref";
    private static final String LOGGED_IN_EMAIL = "logged_in_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(SESSION_PREF, MODE_PRIVATE);

        // ✅ Check if user is already logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User already logged in - go directly to MainActivity
            Toast.makeText(this, "Welcome back! 👋", Toast.LENGTH_SHORT).show();

            // Save session info
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LOGGED_IN_EMAIL, currentUser.getEmail());
            editor.putString("user_name", currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Traveler");
            editor.apply();

            startActivity(new Intent(AuthActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Initialize views
        appName = findViewById(R.id.appName);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Set app name
        if (appName != null) {
            appName.setText("YATRAMITRA");
        }

        // Login Button Click
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                startActivity(new Intent(AuthActivity.this, LoginActivity.class));
            });
        }

        // Register Button Click
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                startActivity(new Intent(AuthActivity.this, RegisterActivity.class));
            });
        }
    }
}
