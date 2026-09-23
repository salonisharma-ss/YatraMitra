package com.example.localexplorer;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserAuthDatabase {
    private static final String TAG = "UserAuthDatabase";
    private FirebaseAuth firebaseAuth;

    public UserAuthDatabase(Context context) {
        // Initialize Firebase Auth
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    // ✅ Register new user with email and password
    public void registerUser(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "Registration successful: " + email);
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Registration failed";
                        Log.e(TAG, "Registration error: " + error);
                        callback.onError(error);
                    }
                });
    }

    // ✅ Login user with email and password
    public void loginUser(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "Login successful: " + email);
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Login failed";
                        Log.e(TAG, "Login error: " + error);
                        callback.onError(error);
                    }
                });
    }

    // ✅ Check if user is already logged in
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // ✅ Get user email
    public String getUserEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    // ✅ Logout user
    public void logoutUser() {
        firebaseAuth.signOut();
        Log.d(TAG, "User logged out");
    }

    // ✅ Callback interface for async operations
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }
}