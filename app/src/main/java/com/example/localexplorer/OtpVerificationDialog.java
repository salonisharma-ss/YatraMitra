package com.example.localexplorer;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OtpVerificationDialog {
    private Context context;
    private AlertDialog dialog;
    private OtpVerificationCallback callback;
    private String generatedOtp;
    private long startTime;

    public interface OtpVerificationCallback {
        void onOtpVerified(String otp);
        void onCancel();
    }

    public OtpVerificationDialog(Context context, String email, String generatedOtp, OtpVerificationCallback callback) {
        this.context = context;
        this.generatedOtp = generatedOtp;
        this.callback = callback;
        this.startTime = System.currentTimeMillis();

        createDialog(email);
    }

    private void createDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);

        // Custom layout के लिए LayoutInflater use करेंगे
        // For now, simple approach

        builder.setTitle("🔐 Verify Email");
        builder.setMessage("OTP भेज दिया गया है:\n" + email);

        final EditText otpInput = new EditText(context);
        otpInput.setHint("6-digit OTP enter करो");
        otpInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(otpInput);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String enteredOtp = otpInput.getText().toString().trim();

            if (enteredOtp.isEmpty()) {
                Toast.makeText(context, "❌ OTP enter करो", Toast.LENGTH_SHORT).show();
                return;
            }

            if (enteredOtp.equals(generatedOtp)) {
                Toast.makeText(context, "✅ OTP verified!", Toast.LENGTH_SHORT).show();
                callback.onOtpVerified(enteredOtp);
            } else {
                Toast.makeText(context, "❌ Wrong OTP! Try again", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            callback.onCancel();
        });

        dialog = builder.create();
        dialog.show();
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }
}