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
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button btnRegister;
    private Button btnBack;
    private TextView btnLoginLink;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private static final String SESSION_PREF = "session_pref";
    private static final String LOGGED_IN_EMAIL = "logged_in_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(SESSION_PREF, MODE_PRIVATE);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        btnLoginLink = findViewById(R.id.btnLoginLink);

        if (btnRegister != null) btnRegister.setOnClickListener(v -> performRegister());
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        if (btnLoginLink != null) {
            btnLoginLink.setOnClickListener(v -> {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            });
        }
    }

    private void performRegister() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        String generatedOtp = OtpManager.generateOTP(email);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending OTP...");
        progressDialog.show();

        EmailService.sendOTPEmail(email, generatedOtp, new EmailService.EmailCallback() {
            @Override
            public void onSuccess() {
                if (progressDialog != null) progressDialog.dismiss();
                
                OtpDialogHelper.showOtpDialog(RegisterActivity.this, email, new OtpDialogHelper.OtpCallback() {
                    @Override
                    public void onOtpVerified() {
                        registerUserInFirebase(name, email, password);
                    }

                    @Override
                    public void onCancel() {
                        showError("Verification cancelled");
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (progressDialog != null) progressDialog.dismiss();
                showError("Failed to send OTP: " + error);
            }
        });
    }

    private void registerUserInFirebase(String name, String email, String password) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (progressDialog != null) progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();
                            user.updateProfile(profile);
                            
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(LOGGED_IN_EMAIL, email);
                            editor.apply();

                            showSuccess("Welcome " + name + "!");
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        showError(task.getException().getMessage());
                    }
                });
    }

    private void showSuccess(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(android.graphics.Color.GREEN).show();
    }

    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(android.graphics.Color.RED).show();
    }
}
