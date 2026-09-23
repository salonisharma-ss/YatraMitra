package com.example.localexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReviewsDatabase {
    private static final String PREF_NAME = "reviews_pref";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public ReviewsDatabase(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    // Add review for a place
    public void addReview(String placeName, ReviewModel review) {
        List<ReviewModel> reviews = getReviewsForPlace(placeName);
        reviews.add(0, review); // Add to beginning

        String json = gson.toJson(reviews);
        sharedPreferences.edit().putString(placeName, json).apply();
    }

    // Get all reviews for a place
    public List<ReviewModel> getReviewsForPlace(String placeName) {
        String json = sharedPreferences.getString(placeName, "[]");
        Type type = new TypeToken<List<ReviewModel>>(){}.getType();
        return gson.fromJson(json, type);
    }

    // Delete review
    public void deleteReview(String placeName, ReviewModel review) {
        List<ReviewModel> reviews = getReviewsForPlace(placeName);
        reviews.removeIf(r -> r.getUserEmail().equals(review.getUserEmail()) &&
                r.getDate().equals(review.getDate()));

        String json = gson.toJson(reviews);
        sharedPreferences.edit().putString(placeName, json).apply();
    }

    // Get average rating for a place
    public double getAverageRating(String placeName) {
        List<ReviewModel> reviews = getReviewsForPlace(placeName);
        if (reviews.isEmpty()) return 0;

        double total = 0;
        for (ReviewModel review : reviews) {
            total += review.getRating();
        }
        return total / reviews.size();
    }

    // Clear all reviews for a place
    public void clearReviews(String placeName) {
        sharedPreferences.edit().remove(placeName).apply();
    }

    // ✅ Get all reviews count (Fixed)
    public int getAllReviewsCount() {
        int totalCount = 0;

        // Get all reviews from SharedPreferences
        java.util.Map<String, ?> allEntries = sharedPreferences.getAll();

        for (java.util.Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = (String) entry.getValue();
            Type type = new TypeToken<List<ReviewModel>>(){}.getType();
            List<ReviewModel> reviews = gson.fromJson(json, type);

            if (reviews != null) {
                totalCount += reviews.size();
            }
        }

        return totalCount;
    }
}