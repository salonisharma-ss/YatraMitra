package com.example.localexplorer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private LinearLayout favoritesList;
    private FavoritesDatabase favDB;
    private Button btnBack;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesList = findViewById(R.id.favoritesList);
        btnBack = findViewById(R.id.btnBack);
        emptyMessage = findViewById(R.id.emptyMessage);

        favDB = new FavoritesDatabase(this);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadFavorites();
    }

    private void loadFavorites() {
        List<String> favorites = favDB.getAllFavorites();
        favoritesList.removeAllViews();

        if (favorites == null || favorites.isEmpty()) {
            if (emptyMessage != null) {
                emptyMessage.setText("❤️ No favorites yet!\n\nAdd places to your favorites to see them here.");
                emptyMessage.setVisibility(android.view.View.VISIBLE);
            }
            return;
        }

        if (emptyMessage != null) {
            emptyMessage.setVisibility(android.view.View.GONE);
        }

        for (String favoritePlace : favorites) {
            LinearLayout favoriteCard = new LinearLayout(this);
            favoriteCard.setOrientation(LinearLayout.HORIZONTAL);
            favoriteCard.setPadding(16, 16, 16, 16);
            favoriteCard.setBackgroundResource(R.drawable.place_card_bg);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 12);
            favoriteCard.setLayoutParams(cardParams);

            // Emoji
            TextView emojiView = new TextView(this);
            emojiView.setText("❤️");
            emojiView.setTextSize(32);
            emojiView.setPadding(12, 0, 12, 0);
            LinearLayout.LayoutParams emojiParams = new LinearLayout.LayoutParams(80, 80);
            emojiView.setLayoutParams(emojiParams);

            // Info Layout
            LinearLayout infoLayout = new LinearLayout(this);
            infoLayout.setOrientation(LinearLayout.VERTICAL);
            infoLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            infoLayout.setPadding(0, 0, 12, 0);

            // Place Name
            TextView nameView = new TextView(this);
            nameView.setText(favoritePlace);
            nameView.setTextSize(16);
            nameView.setTextColor(0xFF1976D2);
            nameView.setTypeface(null, android.graphics.Typeface.BOLD);
            infoLayout.addView(nameView);

            // Address
            String address = favDB.getFavoriteAddress(favoritePlace);
            if (address != null && !address.isEmpty()) {
                TextView addressView = new TextView(this);
                addressView.setText("📍 " + address);
                addressView.setTextSize(12);
                addressView.setTextColor(0xFF666666);
                addressView.setPadding(0, 4, 0, 0);
                infoLayout.addView(addressView);
            }

            // Remove Button
            Button btnRemove = new Button(this);
            btnRemove.setText("Remove");
            btnRemove.setTextSize(12);
            btnRemove.setPadding(16, 0, 16, 0);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnRemove.setLayoutParams(btnParams);

            final String placeToRemove = favoritePlace;
            btnRemove.setOnClickListener(v -> {
                favDB.removeFavorite(placeToRemove);
                Toast.makeText(FavoritesActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                loadFavorites();
            });

            favoriteCard.addView(emojiView);
            favoriteCard.addView(infoLayout);
            favoriteCard.addView(btnRemove);

            favoritesList.addView(favoriteCard);
        }
    }
}