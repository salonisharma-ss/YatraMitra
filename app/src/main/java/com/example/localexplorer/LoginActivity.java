package com.example.localexplorer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button btnLogin;
    private Button btnBack;
    private TextView btnRegisterLink;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private static final String SESSION_PREF = "session_pref";
    private static final String LOGGED_IN_EMAIL = "logged_in_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(SESSION_PREF, MODE_PRIVATE);

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        btnRegisterLink = findViewById(R.id.btnRegisterLink);

        // Login Button Click
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> performLogin());
        }

        // Back Button Click
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Register Link Click
        if (btnRegisterLink != null) {
            btnRegisterLink.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            });
        }
    }

    // ✅ Perform login
    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            showError("Please enter email");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter password");
            return;
        }

        if (!email.contains("@")) {
            showError("Please enter valid email");
            return;
        }

        // ✅ Generate OTP
        String generatedOtp = OtpManager.generateOTP(email);

        // ✅ Show loading - sending email
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Sending OTP to your email...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // ✅ Send OTP via Email
        EmailService.sendOTPEmail(email, generatedOtp, new EmailService.EmailCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    // ✅ Show OTP input dialog
                    OtpDialogHelper.showOtpDialog(LoginActivity.this, email, new OtpDialogHelper.OtpCallback() {
                        @Override
                        public void onOtpVerified() {
                            // ✅ OTP verified - अब Firebase login करो
                            loginUserInFirebase(email, password);
                        }

                        @Override
                        public void onCancel() {
                            showError("❌ Login cancelled");
                        }
                    });
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showError("Failed to send OTP: " + error);
                });
            }
        });
    }

    // ✅ Login user in Firebase (after OTP verification)
    private void loginUserInFirebase(String email, String password) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {
                            String userName = user.getDisplayName() != null ? user.getDisplayName() : "Traveler";

                            // Save login session
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(LOGGED_IN_EMAIL, email);
                            editor.putString("user_name", userName);
                            editor.apply();

                            showSuccess("✅ Login successful!\n👋 Welcome " + userName);

                            // Go to MainActivity
                            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }, 2000);
                        }
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Login failed";
                        showError("❌ " + error);
                    }
                });
    }

    // ✅ Better Notifications
    private void showSuccess(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(android.graphics.Color.parseColor("#4CAF50")); // Green
        snackbar.setTextColor(android.graphics.Color.WHITE);
        snackbar.setDuration(3000);
        snackbar.show();
    }

    private void showError(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(android.graphics.Color.parseColor("#F44336")); // Red
        snackbar.setTextColor(android.graphics.Color.WHITE);
        snackbar.setDuration(4000);
        snackbar.show();
    }
}
