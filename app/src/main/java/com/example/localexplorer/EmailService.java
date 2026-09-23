package com.example.localexplorer;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class EmailService {

    // ⚠️ Replace with your SendGrid API Key
    private static final String SENDGRID_API_KEY = "YOUR_SENDGRID_API_KEY";
    private static final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";

    // ⚠️ IMPORTANT: Change this to the email address you verified in SendGrid (Sender Authentication)
    // If you use an unverified email, you will get a 403 error.
    private static final String SENDER_EMAIL = "s.sharma.workmail@gmail.com";

    public interface EmailCallback {
        void onSuccess();
        void onError(String error);
    }

    public static void sendOTPEmail(String recipientEmail, String otp, EmailCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        try {
            JSONObject payload = new JSONObject();

            // From email object
            JSONObject from = new JSONObject();
            from.put("email", SENDER_EMAIL); 
            from.put("name", "YatraMitra");
            payload.put("from", from);

            payload.put("subject", "YatraMitra - Email Verification OTP");

            // Personalizations
            JSONArray personalizations = new JSONArray();
            JSONObject personalization = new JSONObject();
            JSONArray toArray = new JSONArray();
            JSONObject to = new JSONObject();
            to.put("email", recipientEmail);
            toArray.put(to);
            personalization.put("to", toArray);
            personalizations.put(personalization);
            payload.put("personalizations", personalizations);

            // Content
            JSONArray contentArray = new JSONArray();
            JSONObject content = new JSONObject();
            content.put("type", "text/html");
            content.put("value", getHtmlContent(otp));
            contentArray.put(content);
            payload.put("content", contentArray);

            RequestBody body = RequestBody.create(
                    payload.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(SENDGRID_API_URL)
                    .addHeader("Authorization", "Bearer " + SENDGRID_API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        mainHandler.post(callback::onSuccess);
                    } else {
                        Log.e("EmailService", "Error " + response.code() + ": " + responseBody);
                        String msg = "SendGrid Error " + response.code();
                        if (response.code() == 403) {
                            msg += " (Unverified Sender Email)";
                        }
                        String finalMsg = msg;
                        mainHandler.post(() -> callback.onError(finalMsg));
                    }
                    response.close();
                }
            });

        } catch (Exception e) {
            mainHandler.post(() -> callback.onError("Configuration error: " + e.getMessage()));
        }
    }

    private static String getHtmlContent(String otp) {
        return "<html><body style='font-family:sans-serif; text-align:center;'>" +
                "<h2>Email Verification</h2>" +
                "<p>Your OTP for YatraMitra is:</p>" +
                "<h1 style='color:#FF5722; font-size:40px; letter-spacing:5px;'>" + otp + "</h1>" +
                "<p>This code expires in 10 minutes.</p>" +
                "</body></html>";
    }
}
