package com.example.localexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlaceDetailActivity extends AppCompatActivity {

    private TextView placeName;
    private TextView placeRating;
    private TextView placeAddress;
    private TextView placePrice;
    private ImageView placeImage;
    private Button btnShare;
    private Button btnFavorite;
    private Button btnCall;
    private Button btnMaps;
    private Button btnSubmitReview;
    private Button btnViewReviews;
    private EditText nameInput;
    private EditText reviewInput;
    private RatingBar ratingBar;
    private LinearLayout reviewsList;
    private ScrollView reviewsScrollView;
    private FavoritesDatabase favDB;
    private ReviewsDatabase reviewsDB;
    private String placeTitle = "Unknown";
    private double latitude = 0;
    private double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        favDB = new FavoritesDatabase(this);
        reviewsDB = new ReviewsDatabase(this);

        // Initialize views
        placeName = findViewById(R.id.placeName);
        placeRating = findViewById(R.id.placeRating);
        placeAddress = findViewById(R.id.placeAddress);
        placePrice = findViewById(R.id.placePrice);
        placeImage = findViewById(R.id.placeImage);
        btnShare = findViewById(R.id.btnShare);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnCall = findViewById(R.id.btnCall);
        btnMaps = findViewById(R.id.btnMaps);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        btnViewReviews = findViewById(R.id.btnViewReviews);
        nameInput = findViewById(R.id.nameInput);
        reviewInput = findViewById(R.id.reviewInput);
        ratingBar = findViewById(R.id.ratingBar);
        reviewsList = findViewById(R.id.reviewsList);
        reviewsScrollView = findViewById(R.id.reviewsScrollView);

        // Get intent data
        Intent intent = getIntent();
        String placeId = null;

        if (intent != null) {
            placeTitle = intent.getStringExtra("name");
            if (placeTitle == null) placeTitle = "Unknown";

            latitude = intent.getDoubleExtra("latitude", 0);
            longitude = intent.getDoubleExtra("longitude", 0);
            placeId = intent.getStringExtra("placeId");

            String rating = intent.getStringExtra("rating");
            if (rating == null) rating = "No rating";

            String address = intent.getStringExtra("address");
            if (address == null) address = "No address";

            String price = intent.getStringExtra("price");
            if (price == null) price = "₹ Budget";

            String photoUrl = intent.getStringExtra("photoUrl");

            // Set UI
            if (placeName != null) placeName.setText(placeTitle);
            if (placeRating != null) placeRating.setText(rating);
            if (placeAddress != null) placeAddress.setText(address);
            if (placePrice != null) placePrice.setText(price);

            // Load Image
            if (placeImage != null && photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.place_card_bg) // Use existing bg as placeholder
                        .error(R.drawable.ic_launcher_background)
                        .into(placeImage);
            }
        }

        // Button listeners
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> sharePlace());
        }

        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> addFavorite());
        }

        if (btnCall != null) {
            btnCall.setOnClickListener(v -> Toast.makeText(this, "Calling...", Toast.LENGTH_SHORT).show());
        }

        if (btnMaps != null) {
            btnMaps.setOnClickListener(v -> {
                if (latitude > 0 && longitude > 0) {
                    Intent mapIntent = new Intent(PlaceDetailActivity.this, MapActivity.class);
                    mapIntent.putExtra("latitude", latitude);
                    mapIntent.putExtra("longitude", longitude);
                    mapIntent.putExtra("placeName", placeTitle);
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(this, "Location data not available", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnSubmitReview != null) {
            btnSubmitReview.setOnClickListener(v -> submitReview(placeTitle));
        }

        if (btnViewReviews != null) {
            btnViewReviews.setOnClickListener(v -> toggleReviewsVisibility());
        }

        // Load Google reviews if placeId available
        final String finalPlaceId = placeId;
        if (finalPlaceId != null && !finalPlaceId.isEmpty()) {
            loadGoogleReviews(finalPlaceId);
        } else {
            displayReviews(placeTitle);
        }
    }

    private void loadGoogleReviews(String placeId) {
        GooglePlacesHelper.getPlaceDetails(this, placeId, new GooglePlacesHelper.PlaceDetailsCallback() {
            @Override
            public void onSuccess(List<ReviewModel> googleReviews) {
                List<ReviewModel> localReviews = reviewsDB.getReviewsForPlace(placeTitle);
                if (googleReviews != null && !googleReviews.isEmpty()) {
                    googleReviews.addAll(localReviews);
                } else {
                    googleReviews = localReviews;
                }
                displayAllReviews(googleReviews);
            }

            @Override
            public void onError(String error) {
                Log.e("PlaceDetail", "Google Reviews Error: " + error);
                displayReviews(placeTitle);
            }
        });
    }

    private void displayAllReviews(List<ReviewModel> reviews) {
        if (reviewsList == null) return;
        reviewsList.removeAllViews();
        if (reviews == null || reviews.isEmpty()) {
            TextView noReviews = new TextView(this);
            noReviews.setText("No reviews yet");
            noReviews.setPadding(16, 16, 16, 16);
            reviewsList.addView(noReviews);
            return;
        }

        for (ReviewModel review : reviews) {
            LinearLayout reviewCard = new LinearLayout(this);
            reviewCard.setOrientation(LinearLayout.VERTICAL);
            reviewCard.setPadding(12, 12, 12, 12);
            reviewCard.setBackgroundColor(0xFFF0F0F0);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 10);
            reviewCard.setLayoutParams(params);

            TextView userView = new TextView(this);
            userView.setText(review.getUserName() + " • ⭐ " + review.getRating());
            userView.setTextSize(14);
            userView.setTextColor(0xFF1976D2);
            userView.setTypeface(null, android.graphics.Typeface.BOLD);
            reviewCard.addView(userView);

            TextView reviewView = new TextView(this);
            reviewView.setText(review.getReviewText());
            reviewView.setTextSize(13);
            reviewView.setTextColor(0xFF333333);
            reviewView.setPadding(0, 8, 0, 0);
            reviewCard.addView(reviewView);

            TextView dateView = new TextView(this);
            dateView.setText(review.getDate());
            dateView.setTextSize(11);
            dateView.setTextColor(0xFF999999);
            dateView.setPadding(0, 8, 0, 0);
            reviewCard.addView(dateView);

            reviewsList.addView(reviewCard);
        }
    }

    private void sharePlace() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String ratingText = placeRating != null ? placeRating.getText().toString() : "No rating";
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + placeTitle + "! " + ratingText);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void addFavorite() {
        if (placeTitle == null || placeTitle.isEmpty() || placeTitle.equals("Unknown")) {
            Toast.makeText(this, "Place name not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String addressText = placeAddress != null ? placeAddress.getText().toString() : "No address";
        if (favDB != null) {
            favDB.addFavorite(placeTitle, addressText);
            Toast.makeText(this, "Added to favorites! ❤️", Toast.LENGTH_SHORT).show();
            if (btnFavorite != null) {
                btnFavorite.setBackgroundColor(0xFFE91E63);
                btnFavorite.setText("❤️ Favorited");
                btnFavorite.setEnabled(false);
            }
        }
    }

    private void submitReview(String placeName) {
        String userName = nameInput.getText().toString().trim();
        String reviewText = reviewInput.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (userName.isEmpty() || reviewText.isEmpty() || rating == 0) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ReviewModel review = new ReviewModel();
        review.setUserName(userName);
        review.setUserEmail("user@example.com");
        review.setRating(rating);
        review.setReviewText(reviewText);
        review.setDate(new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(new Date()));

        if (reviewsDB != null) {
            reviewsDB.addReview(placeName, review);
        }

        nameInput.setText("");
        reviewInput.setText("");
        ratingBar.setRating(0);

        Toast.makeText(this, "Review added!", Toast.LENGTH_SHORT).show();
        displayReviews(placeName);
    }

    private void displayReviews(String placeName) {
        if (reviewsList == null || reviewsDB == null) return;
        List<ReviewModel> reviews = reviewsDB.getReviewsForPlace(placeName);
        reviewsList.removeAllViews();

        if (reviews == null || reviews.isEmpty()) {
            TextView noReviews = new TextView(this);
            noReviews.setText("No reviews yet");
            noReviews.setPadding(16, 16, 16, 16);
            reviewsList.addView(noReviews);
        } else {
            for (ReviewModel review : reviews) {
                LinearLayout reviewCard = new LinearLayout(this);
                reviewCard.setOrientation(LinearLayout.VERTICAL);
                reviewCard.setPadding(12, 12, 12, 12);
                reviewCard.setBackgroundColor(0xFFF0F0F0);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 10);
                reviewCard.setLayoutParams(params);

                TextView userView = new TextView(this);
                userView.setText(review.getUserName() + " • ⭐ " + review.getRating());
                userView.setTextSize(14);
                userView.setTextColor(0xFF1976D2);
                userView.setTypeface(null, android.graphics.Typeface.BOLD);
                reviewCard.addView(userView);

                TextView reviewView = new TextView(this);
                reviewView.setText(review.getReviewText());
                reviewView.setTextSize(13);
                reviewView.setTextColor(0xFF333333);
                reviewView.setPadding(0, 8, 0, 0);
                reviewCard.addView(reviewView);

                TextView dateView = new TextView(this);
                dateView.setText(review.getDate());
                dateView.setTextSize(11);
                dateView.setTextColor(0xFF999999);
                dateView.setPadding(0, 8, 0, 0);
                reviewCard.addView(dateView);

                reviewsList.addView(reviewCard);
            }
        }
    }

    private void toggleReviewsVisibility() {
        if (reviewsScrollView == null) return;
        if (reviewsScrollView.getVisibility() == android.view.View.GONE) {
            reviewsScrollView.setVisibility(android.view.View.VISIBLE);
            if (btnViewReviews != null) btnViewReviews.setText("Hide Reviews");
        } else {
            reviewsScrollView.setVisibility(android.view.View.GONE);
            if (btnViewReviews != null) btnViewReviews.setText("View Reviews");
        }
    }
}