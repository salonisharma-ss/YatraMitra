package com.example.localexplorer;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class OtpHelper {
    private FirebaseAuth firebaseAuth;
    private static final long OTP_TIMEOUT = 60; // 60 seconds

    public OtpHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // ✅ Send OTP via Email (generate random 6-digit code)
    public String generateAndSendOTP(String email, OtpCallback callback) {
        String otp = String.format("%06d", (int)(Math.random() * 1000000));

        // Firebase Cloud Functions से भे��ेंगे (अभी demo के लिए)
        // For now, return OTP (production में email service use करेंगे)
        callback.onOtpGenerated(otp);
        return otp;
    }

    // ✅ Verify OTP
    public boolean verifyOTP(String enteredOtp, String generatedOtp) {
        return enteredOtp.equals(generatedOtp);
    }

    public interface OtpCallback {
        void onOtpGenerated(String otp);
        void onError(String error);
    }
}