package com.example.localexplorer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OtpDialogHelper {

    public static void showOtpDialog(Context context, String email, OtpCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Email Verification");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);

        // Message
        TextView message = new TextView(context);
        message.setText("OTP has been sent to:\n" + email + "\n\nPlease check your email and enter the 6-digit OTP below.");
        message.setTextSize(14);
        message.setTextColor(android.graphics.Color.BLACK);
        message.setPadding(0, 0, 0, 20);
        layout.addView(message);

        // Input
        EditText otpInput = new EditText(context);
        otpInput.setHint("Enter 6-digit OTP");
        otpInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        otpInput.setPadding(15, 15, 15, 15);
        otpInput.setTextSize(18);
        layout.addView(otpInput);

        builder.setView(layout);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String enteredOtp = otpInput.getText().toString().trim();

            if (enteredOtp.isEmpty()) {
                android.widget.Toast.makeText(context, "Please enter OTP", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            if (OtpManager.verifyOTP(email, enteredOtp)) {
                android.widget.Toast.makeText(context, "Email verified successfully!", android.widget.Toast.LENGTH_LONG).show();
                callback.onOtpVerified();
            } else {
                android.widget.Toast.makeText(context, "Wrong OTP! Try again", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            callback.onCancel();
        });

        builder.setCancelable(false);
        builder.show();
    }

    public interface OtpCallback {
        void onOtpVerified();
        void onCancel();
    }
}