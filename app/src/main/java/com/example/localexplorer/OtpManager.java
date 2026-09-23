package com.example.localexplorer;

import java.util.HashMap;
import java.util.Map;

public class OtpManager {
    private static final Map<String, String> otpMap = new HashMap<>();

    // ✅ Generate OTP (6 digits)
    public static String generateOTP(String email) {
        String otp = String.format("%06d", (int)(Math.random() * 1000000));
        otpMap.put(email, otp);

        // 10 minutes के बाद OTP expire हो जाएगा
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            otpMap.remove(email);
        }, 10 * 60 * 1000);

        return otp;
    }

    // ✅ Verify OTP
    public static boolean verifyOTP(String email, String enteredOtp) {
        String storedOtp = otpMap.get(email);
        if (storedOtp != null && storedOtp.equals(enteredOtp)) {
            otpMap.remove(email); // OTP use हो गया, delete करो
            return true;
        }
        return false;
    }

    // ✅ Get stored OTP (testing के लिए)
    public static String getOTP(String email) {
        return otpMap.getOrDefault(email, "");
    }
    public static void debugPrintOTP(String email) {
        String otp = otpMap.get(email);
        android.util.Log.d("OTP_DEBUG", "OTP for " + email + " is: " + otp);
    }
}
// ✅ Debug - print OTP to Logcat
